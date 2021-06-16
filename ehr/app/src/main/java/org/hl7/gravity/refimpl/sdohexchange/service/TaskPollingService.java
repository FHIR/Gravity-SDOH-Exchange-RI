package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskOutputComponent;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.hl7.fhir.r4.model.codesystems.SearchModifierCode;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor.TaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TasksPollingBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TasksPollingBundleExtractor.TaskPollingUpdateException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TasksPollingBundleExtractor.TasksPollingInfo;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskFailBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.service.CpService.CpClientException;
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
public class TaskPollingService {

  public static final ArrayList<TaskStatus> FINISHED_TASK_STATUSES = Lists.newArrayList(Task.TaskStatus.FAILED,
      Task.TaskStatus.REJECTED, Task.TaskStatus.COMPLETED, Task.TaskStatus.CANCELLED);

  private final IGenericClient openEhrClient;
  private final CpService cpService;

  @Scheduled(fixedDelayString = "${scheduling.task-polling-delay-millis}")
  public void updateTasks() {
    log.info("Updating tasks from CP Organizations...");
    Bundle tasksBundle = openEhrClient.search()
        .forResource(Task.class)
        .include(Task.INCLUDE_FOCUS)
        .include(Task.INCLUDE_OWNER)
        .include(Organization.INCLUDE_ENDPOINT.setRecurse(true))
        // Get only tasks sent to CP
        .where(new TokenClientParam("owner:Organization.type").exactly()
            .systemAndCode(OrganizationTypeCode.CP.getSystem(), OrganizationTypeCode.CP.toCode()))
        // Get only tasks in-progress
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.FAILED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.REJECTED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.COMPLETED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.CANCELLED.toCode()))
        .where(Task.AUTHORED_ON.before()
            .millis(Date.from(LocalDateTime.now()
                .minusSeconds(10)
                .atZone(ZoneId.systemDefault())
                .toInstant())))
        .returnBundle(Bundle.class)
        .execute();

    TasksPollingInfo tasksPollingInfo = new TasksPollingBundleExtractor().extract(tasksBundle);
    //Collect all entries from every Task bundle for performance considerations.
    Bundle updateBundle = new Bundle();
    updateBundle.setType(Bundle.BundleType.TRANSACTION);

    for (Task task : tasksPollingInfo.getTasks()) {
      ServiceRequest serviceRequest = tasksPollingInfo.getServiceRequest(task);
      Organization organization = tasksPollingInfo.getOrganization(task);
      try {
        Endpoint endpoint = tasksPollingInfo.getEndpoint(organization);
        combineResult(updateBundle, getUpdateBundle(task, serviceRequest, endpoint));
      } catch (TaskPollingUpdateException | CpClientException exc) {
        combineResult(updateBundle, failTask(task, serviceRequest, exc.getMessage()));
      }
    }
    //If there is at least one bundle entry - execute a transaction request.
    if (updateBundle.getEntry()
        .size() != 0) {
      log.info("One or more tasks were changed. Storing updates to EHR...");
      openEhrClient.transaction()
          .withBundle(updateBundle)
          .execute();
    }
    log.info("Task update process finished.");
  }

  protected Bundle getUpdateBundle(Task task, ServiceRequest serviceRequest, Endpoint endpoint)
      throws CpClientException {
    Bundle resultBundle = new Bundle();
    resultBundle.setType(Bundle.BundleType.TRANSACTION);
    // Do a copy not to modify an input Task. Possibly this method will fail during execution, and we don't want to
    // end up with a modified Task.
    Task resultTask = task.copy();

    TaskInfoHolder cpTaskInfo = cpService.read(task.getIdElement()
        .getIdPart(), endpoint);
    Task cpTask = cpTaskInfo.getTask();
    // If status is the same and comments size are the same  OR CP task in REQUESTED state - do nothing.
    if ((cpTask.getStatus()
        .equals(task.getStatus()) && cpTask.getNote()
        .size() == task.getNote()
        .size())
        || Task.TaskStatus.REQUESTED.equals(cpTask.getStatus())) {
      return resultBundle;
    }
    log.info("Task status/field change detected for id '{}'. '{}' -> '{}'. Updating...", task.getIdElement()
        .getIdPart(), task.getStatus(), cpTask.getStatus());
    // Copy required Task fields
    copyTaskFields(resultTask, cpTask, endpoint);
    resultBundle.addEntry(FhirUtil.createPutEntry(resultTask));
    if (FINISHED_TASK_STATUSES.contains(cpTask.getStatus())) {
      // It is critical to pass a resultTask, not a task, since it will be modified inside.
      handleFinishedTask(resultBundle, resultTask, serviceRequest, cpTask, endpoint);
    }
    return resultBundle;
  }

  protected void copyTaskFields(Task ehrTask, Task cpTask, Endpoint endpoint) {
    ehrTask.setStatus(cpTask.getStatus());
    ehrTask.setStatusReason(cpTask.getStatusReason());
    ehrTask.setLastModified(cpTask.getLastModified());
    int ehrNotesSize = ehrTask.getNote()
        .size();
    int cpNotesSize = cpTask.getNote()
        .size();
    if (ehrNotesSize < cpNotesSize) {
      cpTask.getNote()
          .subList(ehrNotesSize, cpNotesSize)
          .stream()
          .filter(cpComment -> cpComment.getAuthor() instanceof Reference)
          .forEach(cpComment -> {
            cpService.externalizeReference(cpComment.getAuthorReference(), endpoint.getAddress());
            ehrTask.addNote(cpComment);
          });
    }
  }

  protected void handleFinishedTask(Bundle resultBundle, Task ehrTask, ServiceRequest ehrServiceRequest, Task cpTask,
      Endpoint endpoint) {
    if (Task.TaskStatus.COMPLETED.equals(cpTask.getStatus())) {
      ehrServiceRequest.setStatus(ServiceRequest.ServiceRequestStatus.COMPLETED);
      resultBundle.addEntry(FhirUtil.createPutEntry(ehrServiceRequest));
    }
    // Procedure should be present if task status is COMPLETED or CANCELLED. Copy it. Also take care of a Task.output
    // property.
    if (Task.TaskStatus.COMPLETED.equals(cpTask.getStatus()) || Task.TaskStatus.CANCELLED.equals(
        cpTask.getStatus())) {
      // Modify Task.output. If task output is of type resulting-activity and contains a Reference to a proper
      // Procedure - copy output changing a Procedure reference to a local one.
      List<TaskOutputComponent> cpOutputs = cpTask.getOutput()
          .stream()
          //We only copy outputs which reference a Code and a Reference
          .filter(t -> t.getValue() instanceof CodeableConcept ||
              (t.getValue() instanceof Reference && ((Reference) t.getValue()).getReferenceElement()
                  .getResourceType()
                  .equals(Procedure.class.getSimpleName())))
          .collect(Collectors.toList());
      if (cpOutputs.size() == 0) {
        log.warn(
            "Not output of type 'http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes|resulting"
                + "-activity' with a reference to a proper Procedure is present in task with id '{}' at '{}'. "
                + "Expecting a reference to a Procedure resource.", cpTask.getIdElement()
                .getIdPart(), endpoint.getAddress());
      }
      Map<String, Procedure> cpProcedureMap = getCpProcedures(cpOutputs, endpoint);
      for (Task.TaskOutputComponent cpOutput : cpOutputs) {
        Task.TaskOutputComponent ehrTaskOutput = cpOutput.copy();
        if (cpOutput.getValue() instanceof Reference) {
          Reference cpProcedureReference = (Reference) cpOutput.getValue();
          String cpProcedureId = cpProcedureReference.getReferenceElement()
              .getIdPart();
          Procedure cpProcedure = cpProcedureMap.get(cpProcedureId);
          // All Procedures have the same reason reference as ServiceRequest
          Procedure resultProcedure =
              copyProcedure(cpProcedure, ehrTask.getFor(), ehrTask.getFocus(), ehrServiceRequest.getReasonReference());
          resultProcedure.setId(IdType.newRandomUuid());
          resultProcedure.addIdentifier()
              .setSystem(endpoint.getAddress())
              .setValue(cpProcedureId);
          // Add Procedure to result bundle
          resultBundle.addEntry(FhirUtil.createPostEntry(resultProcedure));

          ehrTaskOutput.setValue(FhirUtil.toReference(Procedure.class, resultProcedure.getIdElement()
              .getIdPart(), cpProcedureReference.getDisplay()));
        }
        ehrTask.addOutput(ehrTaskOutput);
      }
    }
  }

  protected Map<String, Procedure> getCpProcedures(List<TaskOutputComponent> taskOutputs, Endpoint endpoint) {
    List<String> procedureIds = taskOutputs.stream()
        .map(TaskOutputComponent::getValue)
        .filter(Reference.class::isInstance)
        .map(Reference.class::cast)
        .map(procedureReference -> procedureReference.getReferenceElement()
            .getIdPart())
        .collect(Collectors.toList());
    return cpService.search(procedureIds, Procedure.class, endpoint)
        .getEntry()
        .stream()
        .map(BundleEntryComponent::getResource)
        .filter(Procedure.class::isInstance)
        .map(Procedure.class::cast)
        .collect(Collectors.toMap(p -> p.getIdElement()
            .getIdPart(), Function.identity()));
  }

  private static Procedure copyProcedure(Procedure cpProcedure, Reference patientReference,
      Reference serviceRequest, List<Reference> reasonReferences) {
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