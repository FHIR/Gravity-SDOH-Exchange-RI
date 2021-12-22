package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.exception.TaskUpdateException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.reference.util.ServiceRequestReferenceCollector;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// Copied from cp/app/src/main/java/org/hl7/gravity/refimpl/sdohexchange/fhir/factory
// Deleted user, priorityForCBO, cboTaskRequester and cboTaskOwner fields
// Block with condition to check ACCEPTED status was deleted
@Getter
@Setter
public class TaskUpdateBundleFactory {

  private static final Map<Task.TaskStatus, List<Task.TaskStatus>> TASK_STATE_MACHINE = new HashMap<>();

  private Task task;
  private ServiceRequest serviceRequest;
  private Task.TaskStatus status;
  private String statusReason;
  private String comment;
  private String outcome;
  private List<Coding> procedureCodes;

  static {
    //TODO: Verify this state machine
    TASK_STATE_MACHINE.put(Task.TaskStatus.RECEIVED, Arrays.asList(Task.TaskStatus.REJECTED, Task.TaskStatus.ACCEPTED));
    TASK_STATE_MACHINE.put(Task.TaskStatus.ACCEPTED,
        Arrays.asList(Task.TaskStatus.CANCELLED, Task.TaskStatus.INPROGRESS));
    TASK_STATE_MACHINE.put(Task.TaskStatus.INPROGRESS,
        Arrays.asList(Task.TaskStatus.ONHOLD, Task.TaskStatus.COMPLETED, Task.TaskStatus.CANCELLED));
    TASK_STATE_MACHINE.put(Task.TaskStatus.ONHOLD,
        Arrays.asList(Task.TaskStatus.INPROGRESS, Task.TaskStatus.CANCELLED));
  }

  public Bundle createUpdateBundle() {
    Assert.notNull(task, "Task can't be null.");

    Bundle updateBundle = new Bundle();
    updateBundle.setType(Bundle.BundleType.TRANSACTION);

    updateTask(updateBundle);

    updateBundle.addEntry(FhirUtil.createPutEntry(task));
    return updateBundle;
  }

  private void validateStatus() {
    if (Objects.equals(task.getStatus(), status)) {
      throw new TaskUpdateException("Task status must not be the same.");
    }
    List<Task.TaskStatus> possibleStatuses = TASK_STATE_MACHINE.get(task.getStatus());
    if (possibleStatuses == null || !possibleStatuses.contains(status)) {
      throw new TaskUpdateException(String.format("Unable to update Task status from '%s' to '%s'.", task.getStatus()
          .getDisplay(), status.getDisplay()));
    }
  }

  private void updateTask(Bundle updateBundle) {
    if (status != null) {
      validateStatus();
      task.setStatus(status);
      task.setLastModifiedElement(DateTimeType.now());

      Assert.notNull(serviceRequest, "ServiceRequest can't be null.");
      if (status == Task.TaskStatus.REJECTED || status == Task.TaskStatus.CANCELLED) {
        Assert.notNull(statusReason, "Status reason cannot be null.");
        task.setStatusReason(new CodeableConcept().setText(statusReason));
        if (status == Task.TaskStatus.CANCELLED) {
          createProceduresOutput(updateBundle);
        }
        serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.REVOKED);
        updateBundle.addEntry(FhirUtil.createPutEntry(serviceRequest));
      } else if (status == Task.TaskStatus.COMPLETED) {
        Assert.notNull(outcome, "Outcome cannot be null.");
        Assert.isTrue(procedureCodes.size() > 0, "Procedures can't be empty.");
        task.getOutput()
            .add(createTaskOutput(new CodeableConcept().setText(outcome)));
        createProceduresOutput(updateBundle);

        serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.COMPLETED);
        updateBundle.addEntry(FhirUtil.createPutEntry(serviceRequest));
      }
    }
    if (StringUtils.hasText(comment)) {
      task.addNote()
          .setText(comment)
          .setTimeElement(DateTimeType.now());
    }
  }

  private void createProceduresOutput(Bundle updateBundle) {
    for (Coding code : procedureCodes) {
      Procedure procedure = createProcedure(code);
      updateBundle.addEntry(FhirUtil.createPostEntry(procedure));

      Task.TaskOutputComponent taskOutput = createTaskOutput(FhirUtil.toReference(Procedure.class,
          procedure.getIdElement()
              .getIdPart(), String.format("%s (%s)", code.getDisplay(), code.getCode())));
      task.getOutput()
          .add(taskOutput);
    }
  }

  private Procedure createProcedure(Coding coding) {
    Procedure procedure = new Procedure();
    procedure.getMeta()
        .addProfile(SDOHProfiles.PROCEDURE);
    procedure.setId(IdType.newRandomUuid());
    procedure.setStatus(Procedure.ProcedureStatus.COMPLETED);
    procedure.setCategory(serviceRequest.getCategoryFirstRep());
    procedure.getCode()
        .addCoding(coding);
    procedure.getBasedOn()
        .add(task.getFocus());
    procedure.setSubject(task.getFor());
    procedure.setPerformed(DateTimeType.now());
    procedure.setReasonReference(ServiceRequestReferenceCollector.getConditions(serviceRequest));
    return procedure;
  }

  private static Task.TaskOutputComponent createTaskOutput(Type value) {
    Task.TaskOutputComponent taskOutput = new Task.TaskOutputComponent();
    taskOutput.setType(new CodeableConcept().addCoding(
        new Coding("http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes", "resulting-activity",
            "Resulting Activity")));
    taskOutput.setValue(value);
    return taskOutput;
  }
}
