package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Condition;
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
 * Service for CBRO interaction. This logic definitely should not have been a service class, but, for simplicity,
 * implemented like this.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CbroTaskUpdateService {

  public static final ArrayList<Task.TaskStatus> FINISHED_TASK_STATUSES = Lists.newArrayList(Task.TaskStatus.FAILED,
      Task.TaskStatus.REJECTED, Task.TaskStatus.COMPLETED, Task.TaskStatus.CANCELLED);

  @Value("${ehr.open-fhir-server-uri}")
  private String identifierSystem;

  public Bundle getUpdateBundle(Task task, IGenericClient ehrClient, IGenericClient cbroClient)
      throws CbroTaskUpdateException {
    Bundle resultBundle = new Bundle();
    resultBundle.setType(Bundle.BundleType.TRANSACTION);

    // Do a copy not to modify an input Task. Possibly this method will fail during execution, and we don't want to
    // end up with a modified Task.
    Task resultTask = task.copy();
    Task cbroTask = fetchTaskFromCbro(task, cbroClient);
    // If status is the same - do nothing. Also CBRO status might still be received whtn EHR's one is accepted. This
    // should be ignored.
    if (cbroTask.getStatus()
        .equals(task.getStatus()) || Task.TaskStatus.REQUESTED.equals(cbroTask.getStatus())) {
      return resultBundle;
    }
    log.info("Task status change detected for id '{}'. '{}' -> '{}'. Updating...", task.getIdElement()
        .getIdPart(), task.getStatus(), cbroTask.getStatus());
    // Copy required Task fields
    resultTask.setStatus(cbroTask.getStatus());
    resultTask.setStatusReason(cbroTask.getStatusReason());
    resultTask.setLastModified(cbroTask.getLastModified());
    resultTask.setNote(cbroTask.getNote());
    resultBundle.addEntry(FhirUtil.createPutEntry(resultTask));

    if (FINISHED_TASK_STATUSES.contains(cbroTask.getStatus())) {
      //It is critical to pass a resultTask, not a task, since it will be modified inside.
      handleFinishedTask(resultBundle, resultTask, ehrClient, cbroTask, cbroClient);
    }
    return resultBundle;
  }

  protected Task fetchTaskFromCbro(Task ehrTask, IGenericClient cbroClient) throws CbroTaskUpdateException {
    String taskId = ehrTask.getIdElement()
        .getIdPart();

    Bundle cbroBundle;
    try {
      // Retrieve just a Task. If status is different - retrieve everything else. This will save some time, since
      // task update will not be performed frequently.
      cbroBundle = cbroClient.search()
          .forResource(Task.class)
          .where(Task.IDENTIFIER.exactly()
              .systemAndValues(identifierSystem, taskId))
          .returnBundle(Bundle.class)
          .execute();
    } catch (BaseServerResponseException exc) {
      throw new CbroTaskUpdateException(
          String.format("Task retrieval failed for identifier '%s' at CBRO location '%s'. Reason: %s.",
              identifierSystem + "|" + taskId, cbroClient.getServerBase(), exc.getMessage()), exc);
    }
    if (cbroBundle.getEntry()
        .size() == 0) {
      throw new CbroTaskUpdateException(
          String.format("No Task is present at '%s' for identifier '%s'.", cbroClient.getServerBase(),
              identifierSystem + "|" + taskId));
    } else if (cbroBundle.getEntry()
        .size() > 1) {
      throw new CbroTaskUpdateException(
          String.format("More than one Task is present at '%s' for identifier '%s'.", cbroClient.getServerBase(),
              identifierSystem + "|" + taskId));
    }
    return FhirUtil.getFromBundle(cbroBundle, Task.class)
        .get(0);
  }

  protected void handleFinishedTask(Bundle resultBundle, Task ehrTask, IGenericClient ehrClient, Task cbroTask,
      IGenericClient cbroClient) throws CbroTaskUpdateException {
    // Check Task.focus is set as expected.
    if (ehrTask.getFocus()
        .getReference() == null || !ServiceRequest.class.getSimpleName()
        .equals(ehrTask.getFocus()
            .getReferenceElement()
            .getResourceType())) {
      throw new CbroTaskUpdateException(String.format("Task.focus is null or not a ServiceRequest for Task '%s'.",
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
    //Update ServiceRequest status to COMPLETED if Task status is COMPLETED
    if (Task.TaskStatus.COMPLETED.equals(cbroTask.getStatus())) {
      serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.COMPLETED);
      resultBundle.addEntry(FhirUtil.createPutEntry(serviceRequest));
    }

    // Procedure should be present if task status is COMPLETED or CANCELLED. Copy it. Also take care of a Task.output
    // property.
    if (Task.TaskStatus.COMPLETED.equals(cbroTask.getStatus()) || Task.TaskStatus.CANCELLED.equals(
        cbroTask.getStatus())) {
      //Modify Task.output. If task output is of type resulting-activity and contains a Reference to a proper
      // Procedure - copy output changing a Procedure reference to a local one.
      List<Task.TaskOutputComponent> cbroOutputs = cbroTask.getOutput()
          .stream()
          //We only copy outputs which reference a Code and a Reference
          .filter(t -> t.getValue() instanceof CodeableConcept || (Reference.class.isInstance(t.getValue())
              && ((Reference) t.getValue()).getReferenceElement()
              .getResourceType()
              .equals(Procedure.class.getSimpleName())))
          .collect(Collectors.toList());

      if (cbroOutputs.size() == 0) {
        log.warn(
            "Not output of type 'http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes|resulting"
                + "-activity' with a reference to a proper Procedure is present in task with id '{}' at '{}'. "
                + "Expecting a reference to a Procedure resource.", cbroTask.getIdElement()
                .getIdPart(), cbroClient.getServerBase());
      }
      for (Task.TaskOutputComponent cbroOutput : cbroOutputs) {
        //TODO fix the conditions check.

        //TODO: Set patient id and update condition references
        Task.TaskOutputComponent newOut = cbroOutput.copy();
        if (cbroOutput.getValue() instanceof Reference) {
          Reference cbroProcedureReference = (Reference) cbroOutput.getValue();

          String cbroProcedureId = cbroProcedureReference.getReferenceElement()
              .getIdPart();
          Procedure cbroProcedure = cbroClient.read()
              .resource(Procedure.class)
              .withId(cbroProcedureId)
              .execute();

          Procedure resultProcedure = copyProcedure(cbroClient, cbroProcedure, ehrTask.getFor(), serviceRequestId);
          resultProcedure.setId(IdType.newRandomUuid());
          resultProcedure.addIdentifier()
              .setSystem(cbroClient.getServerBase())
              .setValue(cbroProcedureId);
          // Add Procedure to result bundle
          resultBundle.addEntry(FhirUtil.createPostEntry(resultProcedure));
          newOut.setValue(FhirUtil.toReference(Procedure.class, resultProcedure.getIdElement()
              .getIdPart(), cbroProcedureReference.getDisplay()));
        }
        ehrTask.addOutput(newOut);
      }
    }
  }

  protected Procedure copyProcedure(IGenericClient cbroClient, Procedure cbroProcedure, Reference patientReference,
      String serviceRequestId) {
    Procedure resultProc = new Procedure();
    resultProc.getMeta()
        .addProfile(SDOHProfiles.PROCEDURE);
    resultProc.addBasedOn(FhirUtil.toReference(ServiceRequest.class, serviceRequestId));
    resultProc.setStatus(cbroProcedure.getStatus());
    resultProc.setStatusReason(cbroProcedure.getStatusReason());
    resultProc.setCategory(cbroProcedure.getCategory());
    resultProc.setCode(cbroProcedure.getCode());
    resultProc.setSubject(patientReference);
    resultProc.setPerformed(cbroProcedure.getPerformed());
    //TODO: Use one query
    List<Reference> reasonReferences = cbroProcedure.getReasonReference()
        .stream()
        .filter(reference -> reference.getReferenceElement()
            .getResourceType()
            .equals(Condition.class.getSimpleName()))
        .map(reference -> cbroClient.read()
            .resource(Condition.class)
            .withId(reference.getReferenceElement()
                .getIdPart())
            .execute())
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
  public static class CbroTaskUpdateException extends Exception {

    public CbroTaskUpdateException(String message) {
      super(message);
    }

    public CbroTaskUpdateException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}