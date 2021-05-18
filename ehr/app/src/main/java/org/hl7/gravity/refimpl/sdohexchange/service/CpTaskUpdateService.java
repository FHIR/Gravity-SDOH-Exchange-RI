package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for CP interaction. This logic definitely should not have been a service class, but, for simplicity,
 * implemented like this.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CpTaskUpdateService {

  public static final ArrayList<Task.TaskStatus> FINISHED_TASK_STATUSES = Lists.newArrayList(Task.TaskStatus.FAILED,
      Task.TaskStatus.REJECTED, Task.TaskStatus.COMPLETED, Task.TaskStatus.CANCELLED);

  private final FhirContext fhirContext;

  @Value("${ehr.open-fhir-server-uri}")
  private String identifierSystem;

  public Bundle getUpdateBundle(Task task, IGenericClient ehrClient, Endpoint endpoint)
      throws CpTaskUpdateException {
    Bundle resultBundle = new Bundle();
    resultBundle.setType(Bundle.BundleType.TRANSACTION);

    IGenericClient cpClient = fhirContext.newRestfulGenericClient(endpoint.getAddress());
    // Do a copy not to modify an input Task. Possibly this method will fail during execution, and we don't want to
    // end up with a modified Task.
    Task resultTask = task.copy();
    Task cpTask = fetchTaskFromCp(task, endpoint);
    // If status is the same - do nothing. Also CP status might still be received when EHR's one is accepted. This
    // should be ignored.
    if (cpTask.getStatus()
        .equals(task.getStatus()) || Task.TaskStatus.REQUESTED.equals(cpTask.getStatus())) {
      return resultBundle;
    }
    log.info("Task status change detected for id '{}'. '{}' -> '{}'. Updating...", task.getIdElement()
        .getIdPart(), task.getStatus(), cpTask.getStatus());
    // Copy required Task fields
    resultTask.setStatus(cpTask.getStatus());
    resultTask.setStatusReason(cpTask.getStatusReason());
    resultTask.setLastModified(cpTask.getLastModified());
    resultTask.setNote(cpTask.getNote());
    resultBundle.addEntry(FhirUtil.createPutEntry(resultTask));

    if (FINISHED_TASK_STATUSES.contains(cpTask.getStatus())) {
      // It is critical to pass a resultTask, not a task, since it will be modified inside.
      handleFinishedTask(resultBundle, resultTask, ehrClient, cpTask, cpClient);
    }
    return resultBundle;
  }

  protected Task fetchTaskFromCp(Task ehrTask, Endpoint endpoint) throws CpTaskUpdateException {
    IGenericClient cpClient = fhirContext.newRestfulGenericClient(endpoint.getAddress());
    String taskId = ehrTask.getIdElement()
        .getIdPart();
    Bundle cpBundle;
    try {
      // Retrieve just a Task. If status is different - retrieve everything else. This will save some time, since
      // task update will not be performed frequently.
      cpBundle = cpClient.search()
          .forResource(Task.class)
          .where(Task.IDENTIFIER.exactly()
              .systemAndValues(identifierSystem, taskId))
          .returnBundle(Bundle.class)
          .execute();
    } catch (BaseServerResponseException exc) {
      throw new CpTaskUpdateException(
          String.format("Task retrieval failed for identifier '%s' at CP location '%s'. Reason: %s.",
              identifierSystem + "|" + taskId, cpClient.getServerBase(), exc.getMessage()), exc);
    }
    if (cpBundle.getEntry()
        .size() == 0) {
      throw new CpTaskUpdateException(
          String.format("No Task is present at '%s' for identifier '%s'.", cpClient.getServerBase(),
              identifierSystem + "|" + taskId));
    } else if (cpBundle.getEntry()
        .size() > 1) {
      throw new CpTaskUpdateException(
          String.format("More than one Task is present at '%s' for identifier '%s'.", cpClient.getServerBase(),
              identifierSystem + "|" + taskId));
    }
    return FhirUtil.getFromBundle(cpBundle, Task.class)
        .get(0);
  }

  protected void handleFinishedTask(Bundle resultBundle, Task ehrTask, IGenericClient ehrClient, Task cpTask,
      IGenericClient cpClient) throws CpTaskUpdateException {
    // Check Task.focus is set as expected.
    if (ehrTask.getFocus()
        .getReference() == null || !ServiceRequest.class.getSimpleName()
        .equals(ehrTask.getFocus()
            .getReferenceElement()
            .getResourceType())) {
      throw new CpTaskUpdateException(String.format("Task.focus is null or not a ServiceRequest for Task '%s'.",
          ehrTask.getIdElement()
              .getIdPart()));
    }

    String serviceRequestId = ehrTask.getFocus()
        .getReferenceElement()
        .getIdPart();
    ServiceRequest serviceRequest = ehrClient.read()
        .resource(ServiceRequest.class)
        .withId(serviceRequestId)
        .execute();
    // Update ServiceRequest status to COMPLETED if Task status is COMPLETED
    if (Task.TaskStatus.COMPLETED.equals(cpTask.getStatus())) {
      serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.COMPLETED);
      resultBundle.addEntry(FhirUtil.createPutEntry(serviceRequest));
    }

    // Procedure should be present if task status is COMPLETED or CANCELLED. Copy it. Also take care of a Task.output
    // property.
    if (Task.TaskStatus.COMPLETED.equals(cpTask.getStatus()) || Task.TaskStatus.CANCELLED.equals(
        cpTask.getStatus())) {
      // Modify Task.output. If task output is of type resulting-activity and contains a Reference to a proper
      // Procedure - copy output changing a Procedure reference to a local one.
      List<Task.TaskOutputComponent> cpOutputs = cpTask.getOutput()
          .stream()
          //We only copy outputs which reference a Code and a Reference
          .filter(t -> t.getValue() instanceof CodeableConcept || (t.getValue() instanceof Reference
              && ((Reference) t.getValue()).getReferenceElement()
              .getResourceType()
              .equals(Procedure.class.getSimpleName())))
          .collect(Collectors.toList());

      if (cpOutputs.size() == 0) {
        log.warn(
            "Not output of type 'http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes|resulting"
                + "-activity' with a reference to a proper Procedure is present in task with id '{}' at '{}'. "
                + "Expecting a reference to a Procedure resource.", cpTask.getIdElement()
                .getIdPart(), cpClient.getServerBase());
      }
      for (Task.TaskOutputComponent cpOutput : cpOutputs) {
        Task.TaskOutputComponent ehrTaskOutput = cpOutput.copy();
        if (cpOutput.getValue() instanceof Reference) {
          Reference cpProcedureReference = (Reference) cpOutput.getValue();

          String cpProcedureId = cpProcedureReference.getReferenceElement()
              .getIdPart();
          Procedure cpProcedure = cpClient.read()
              .resource(Procedure.class)
              .withId(cpProcedureId)
              .execute();

          Procedure resultProcedure = copyProcedure(cpClient, cpProcedure, ehrTask.getFor(), serviceRequestId);
          resultProcedure.setId(IdType.newRandomUuid());
          resultProcedure.addIdentifier()
              .setSystem(cpClient.getServerBase())
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

  protected Procedure copyProcedure(IGenericClient cpClient, Procedure cpProcedure, Reference patientReference,
      String serviceRequestId) {
    Procedure resultProc = new Procedure();
    resultProc.getMeta()
        .addProfile(SDOHProfiles.PROCEDURE);
    resultProc.addBasedOn(FhirUtil.toReference(ServiceRequest.class, serviceRequestId));
    resultProc.setStatus(cpProcedure.getStatus());
    resultProc.setStatusReason(cpProcedure.getStatusReason());
    resultProc.setCategory(cpProcedure.getCategory());
    resultProc.setCode(cpProcedure.getCode());
    resultProc.setSubject(patientReference);
    resultProc.setPerformed(cpProcedure.getPerformed());
    List<String> reasonReferenceIds = cpProcedure.getReasonReference()
        .stream()
        .filter(reference -> reference.getReferenceElement()
            .getResourceType()
            .equals(Condition.class.getSimpleName()))
        .map(resource -> resource.getReferenceElement()
            .getIdPart())
        .collect(Collectors.toList());
    List<Reference> reasonReferences = cpClient.search()
        .forResource(Condition.class)
        .where(Condition.RES_ID.exactly()
            .codes(reasonReferenceIds))
        .returnBundle(Bundle.class)
        .execute()
        .getEntry()
        .stream()
        .map(BundleEntryComponent::getResource)
        .map(Condition.class::cast)
        .map(condition -> FhirUtil.toReference(Condition.class, condition.getIdentifierFirstRep()
            .getValue(), condition.getCode()
            .getCodingFirstRep()
            .getDisplay()))
        .collect(Collectors.toList());
    resultProc.setReasonReference(reasonReferences);
    return resultProc;
  }

  /**
   * Exception that should be thrown when task update process failed. This will usually lead to a Task with a status
   * FAILED and corresponding statusReason.
   */
  public static class CpTaskUpdateException extends Exception {

    public CpTaskUpdateException(String message) {
      super(message);
    }

    public CpTaskUpdateException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}