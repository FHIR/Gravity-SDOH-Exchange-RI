package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskOutputComponent;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.hl7.fhir.r4.model.codesystems.SearchModifierCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.OurTasksPollingBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.OurTasksPollingBundleExtractor.OurTaskPollingUpdateException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.OurTasksPollingBundleExtractor.OurTasksPollingInfo;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskFailBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class OurTaskPollingService {

  public static final ArrayList<TaskStatus> FINISHED_TASK_STATUSES = Lists.newArrayList(TaskStatus.FAILED,
      TaskStatus.REJECTED, TaskStatus.COMPLETED, TaskStatus.CANCELLED);

  private final IGenericClient openCpClient;
  private final IGenericClient cpClient;

  @Scheduled(fixedDelayString = "${scheduling.task-polling-delay-millis}")
  public void updateTasks() {
    log.info("Updating tasks from own tasks...");
    Bundle tasksBundle = openCpClient.search()
        .forResource(Task.class)
        //Include corresponding Own Tasks
        .revInclude(Task.INCLUDE_BASED_ON)
        //Include ServiceRequest for all Tasks, even the included ones.
        .include(Task.INCLUDE_FOCUS.setRecurse(true))
        // Task, retrieved from the EHR, are the ones where basedOn is not set. In other case - these are tasks sent
        // to CBO (own tasks).
        .where(Task.BASED_ON.isMissing(true))
        // Get only tasks in-progress
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(TaskStatus.FAILED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(TaskStatus.REJECTED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(TaskStatus.COMPLETED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(TaskStatus.CANCELLED.toCode()))
        //Give some time for all the related resources to be correctly saved by the requester system.
        .where(Task.AUTHORED_ON.before()
            .millis(Date.from(LocalDateTime.now()
                .minusSeconds(10)
                .atZone(ZoneId.systemDefault())
                .toInstant())))
        .returnBundle(Bundle.class)
        .execute();

    OurTasksPollingInfo tasksPollingInfo = new OurTasksPollingBundleExtractor().extract(tasksBundle);
    //Collect all entries from every Task bundle for performance considerations.
    Bundle updateBundle = new Bundle();
    updateBundle.setType(Bundle.BundleType.TRANSACTION);

    for (Task task : tasksPollingInfo.getTasks()) {
      //Skip for now. We create own tasks automatically when we set a status to Accepted.
      if (task.getStatus()
          .equals(TaskStatus.REQUESTED) || task.getStatus()
          .equals(TaskStatus.RECEIVED)) {
        continue;
      }
      ServiceRequest serviceRequest = getServiceRequest(task);
      try {
        Task ownTask = tasksPollingInfo.getOurTask(task);
        combineResult(updateBundle, getUpdateBundle(task, serviceRequest, ownTask));
      } catch (OurTaskPollingUpdateException exc) {
        combineResult(updateBundle, failTask(task, serviceRequest, exc.getMessage()));
      }
    }
    //If there is at least one bundle entry - execute a transaction request.
    if (updateBundle.getEntry()
        .size() != 0) {
      log.info("One or more tasks were changed. Storing updates to CP...");
      openCpClient.transaction()
          .withBundle(updateBundle)
          .execute();
    }
    log.info("Task update process finished.");
  }

  protected Bundle getUpdateBundle(Task task, ServiceRequest serviceRequest, Task ownTask) {
    Bundle resultBundle = new Bundle();
    resultBundle.setType(Bundle.BundleType.TRANSACTION);
    // Do a copy not to modify an input Task. Possibly this method will fail during execution, and we don't want to
    // end up with a modified Task.
    Task resultTask = task.copy();

    // If status is the same and comments size are the same  OR CP task in REQUESTED state - do nothing.
    if ((ownTask.getStatus()
        .equals(task.getStatus()) && ownTask.getNote()
        .size() == task.getNote()
        .size()) || TaskStatus.REQUESTED.equals(ownTask.getStatus()) || TaskStatus.RECEIVED.equals(
        ownTask.getStatus())) {
      return resultBundle;
    }
    log.info("Task status/field change detected for id '{}'. '{}' -> '{}'. Updating...", task.getIdElement()
        .getIdPart(), task.getStatus(), ownTask.getStatus());
    // Copy required Task fields
    copyTaskFields(resultTask, ownTask);
    resultBundle.addEntry(FhirUtil.createPutEntry(resultTask));
    if (FINISHED_TASK_STATUSES.contains(ownTask.getStatus())) {
      // It is critical to pass a resultTask, not a task, since it will be modified inside.
      handleFinishedTask(resultBundle, resultTask, serviceRequest, ownTask);
    }
    return resultBundle;
  }

  protected void copyTaskFields(Task task, Task ownTask) {
    task.setStatus(ownTask.getStatus());
    task.setStatusReason(ownTask.getStatusReason());
    task.setLastModified(ownTask.getLastModified());
    int notesSize = task.getNote()
        .size();
    int ownNotesSize = ownTask.getNote()
        .size();
    if (notesSize < ownNotesSize) {
      ownTask.getNote()
          .subList(notesSize, ownNotesSize)
          .stream()
          .filter(ownComment -> ownComment.getAuthor() instanceof Reference)
          .forEach(ownComment -> {
            task.addNote(ownComment);
          });
    }
  }

  protected void handleFinishedTask(Bundle resultBundle, Task task, ServiceRequest serviceRequest, Task ownTask) {
    if (TaskStatus.COMPLETED.equals(ownTask.getStatus())) {
      serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.COMPLETED);
      resultBundle.addEntry(FhirUtil.createPutEntry(serviceRequest));
    }
    // Procedure should be present if task status is COMPLETED or CANCELLED. Copy it. Also take care of a Task.output
    // property.
    if (TaskStatus.COMPLETED.equals(ownTask.getStatus()) || TaskStatus.CANCELLED.equals(ownTask.getStatus())) {
      // Modify Task.output. If task output is of type resulting-activity and contains a Reference to a proper
      // Procedure - copy output changing a Procedure reference to a local one.
      List<TaskOutputComponent> ownTaskOutputs = ownTask.getOutput()
          .stream()
          //We only copy outputs which reference a Code and a Reference
          .filter(t -> t.getValue() instanceof CodeableConcept || (t.getValue() instanceof Reference
              && ((Reference) t.getValue()).getReferenceElement()
              .getResourceType()
              .equals(Procedure.class.getSimpleName())))
          .collect(Collectors.toList());
      if (ownTaskOutputs.size() == 0) {
        log.warn(
            "Not output of type 'http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes|resulting"
                + "-activity' with a reference to a proper Procedure is present in task with id '{}'. "
                + "Expecting a reference to a Procedure resource.", ownTask.getIdElement()
                .getIdPart());
      }
      Map<String, Procedure> ownProcedureMap = getOwnTaskProcedures(ownTaskOutputs);
      for (TaskOutputComponent ownTaskOutput : ownTaskOutputs) {
        TaskOutputComponent taskOutput = ownTaskOutput.copy();
        if (ownTaskOutput.getValue() instanceof Reference) {
          Reference ownProcedureReference = (Reference) ownTaskOutput.getValue();
          String ownProcedureId = ownProcedureReference.getReferenceElement()
              .getIdPart();
          Procedure ownProcedure = ownProcedureMap.get(ownProcedureId);
          // All Procedures have the same reason reference as ServiceRequest
          Procedure resultProcedure = copyProcedure(ownProcedure, task.getFor(), task.getFocus(),
              serviceRequest.getReasonReference());
          resultProcedure.setId(IdType.newRandomUuid());
          //TODO add identifier

          //          resultProcedure.addIdentifier()
          //              .setSystem(SERVER_BASE)
          //              .setValue(ownProcedureId);
          // Add Procedure to result bundle
          resultBundle.addEntry(FhirUtil.createPostEntry(resultProcedure));

          taskOutput.setValue(FhirUtil.toReference(Procedure.class, resultProcedure.getIdElement()
              .getIdPart(), ownProcedureReference.getDisplay()));
        }
        task.addOutput(taskOutput);
      }
    }
  }

  private ServiceRequest getServiceRequest(Task task) {
    if (!(task.getFocus()
        .getResource() instanceof ServiceRequest)) {
      throw new IllegalStateException("No ServiceRequest focus is present for task " + task.getIdElement()
          .getIdPart());
    }
    return (ServiceRequest) task.getFocus()
        .getResource();
  }

  protected Map<String, Procedure> getOwnTaskProcedures(List<TaskOutputComponent> taskOutputs) {
    List<String> procedureIds = taskOutputs.stream()
        .map(TaskOutputComponent::getValue)
        .filter(Reference.class::isInstance)
        .map(Reference.class::cast)
        .map(procedureReference -> procedureReference.getReferenceElement()
            .getIdPart())
        .collect(Collectors.toList());
    return openCpClient.search()
        .forResource(Procedure.class)
        .where(Procedure.RES_ID.exactly()
            .codes(procedureIds))
        .returnBundle(Bundle.class)
        .execute()
        .getEntry()
        .stream()
        .map(BundleEntryComponent::getResource)
        .filter(Procedure.class::isInstance)
        .map(Procedure.class::cast)
        .collect(Collectors.toMap(p -> p.getIdElement()
            .getIdPart(), Function.identity()));
  }

  private static Procedure copyProcedure(Procedure cpProcedure, Reference patientReference, Reference serviceRequest,
      List<Reference> reasonReferences) {
    Procedure resultProc = new Procedure();
    resultProc.getMeta()
        .addProfile(SDOHProfiles.PROCEDURE);
    resultProc.addBasedOn(serviceRequest);
    resultProc.setStatus(cpProcedure.getStatus());
    resultProc.setStatusReason(cpProcedure.getStatusReason());
    resultProc.setCategory(cpProcedure.getCategory());
    resultProc.setCode(cpProcedure.getCode());
    resultProc.setSubject(patientReference);
    resultProc.setPerformed(cpProcedure.getPerformed());
    resultProc.setReasonReference(reasonReferences);
    return resultProc;
  }

  private static Bundle failTask(Task task, ServiceRequest serviceRequest, String reason) {
    return new TaskFailBundleFactory(task, serviceRequest, reason).createFailBundle();
  }

  private static void combineResult(Bundle bundle, Bundle combine) {
    bundle.getEntry()
        .addAll(combine.getEntry());
  }
}