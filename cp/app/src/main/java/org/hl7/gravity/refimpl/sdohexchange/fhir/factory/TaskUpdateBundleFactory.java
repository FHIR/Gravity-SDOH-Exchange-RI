package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestStatus;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.InvalidTaskStatusException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.reference.util.ServiceRequestReferenceCollector;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Getter
@RequiredArgsConstructor
public class TaskUpdateBundleFactory {

  private static final Map<TaskStatus, List<TaskStatus>> TASK_STATE_MACHINE = new HashMap<>();

  private final Task task;
  private final ServiceRequest serviceRequest;
  private final Task.TaskStatus status;
  private final String statusReason;
  private final String comment;
  private final String outcome;
  private final List<Coding> procedureCodes;

  @Setter
  private UserDto user;

  static {
    //TODO: Verify this state machine
    TASK_STATE_MACHINE.put(TaskStatus.RECEIVED, Arrays.asList(TaskStatus.REJECTED, TaskStatus.ACCEPTED));
    TASK_STATE_MACHINE.put(TaskStatus.ACCEPTED, Arrays.asList(TaskStatus.CANCELLED, TaskStatus.INPROGRESS));
    TASK_STATE_MACHINE.put(TaskStatus.INPROGRESS, Arrays.asList(TaskStatus.ONHOLD, TaskStatus.COMPLETED,
        TaskStatus.CANCELLED));
    TASK_STATE_MACHINE.put(TaskStatus.ONHOLD, Arrays.asList(TaskStatus.INPROGRESS, TaskStatus.CANCELLED));
  }

  public Bundle createUpdateBundle() {
    Assert.notNull(task, "Task can't be null.");
    Assert.notNull(serviceRequest, "ServiceRequest can't be null.");

    Bundle updateBundle = new Bundle();
    updateBundle.setType(Bundle.BundleType.TRANSACTION);

    validateStatus();
    updateTask(updateBundle);

    updateBundle.addEntry(FhirUtil.createPutEntry(task));
    return updateBundle;
  }

  private void validateStatus() {
    if (Objects.equals(task.getStatus(), status)) {
      throw new InvalidTaskStatusException("Task status must not be the same.");
    }
    List<TaskStatus> possibleStatuses = TASK_STATE_MACHINE.get(task.getStatus());
    if (possibleStatuses == null || !possibleStatuses.contains(status)) {
      throw new InvalidTaskStatusException(String.format("Unable to update Task status from '%s' to '%s'.",
          task.getStatus()
              .getDisplay(), status.getDisplay()));
    }
  }

  private void updateTask(Bundle updateBundle) {
    task.setStatus(status);
    task.setLastModifiedElement(DateTimeType.now());
    if (StringUtils.hasText(comment)) {
      Assert.notNull(user, "User cannot be null.");
      task.addNote()
          .setText(comment)
          .setTimeElement(DateTimeType.now())
          .setAuthor(new Reference(new IdType(user.getUserType(), user.getId())).setDisplay(user.getName()));
    }
    if (status == TaskStatus.REJECTED || status == TaskStatus.CANCELLED) {
      Assert.notNull(statusReason, "Status reason cannot be null.");
      task.setStatusReason(new CodeableConcept().setText(statusReason));
      if (status == TaskStatus.CANCELLED) {
        createProceduresOutput(updateBundle);
      }
      serviceRequest.setStatus(ServiceRequestStatus.REVOKED);
      updateBundle.addEntry(FhirUtil.createPutEntry(serviceRequest));
    }
    if (status == Task.TaskStatus.COMPLETED) {
      Assert.notNull(outcome, "Outcome cannot be null.");
      Assert.isTrue(procedureCodes.size() > 0, "Procedures can't be empty.");
      task.getOutput()
          .add(createTaskOutput(new CodeableConcept().setText(outcome)));
      createProceduresOutput(updateBundle);

      serviceRequest.setStatus(ServiceRequestStatus.COMPLETED);
      updateBundle.addEntry(FhirUtil.createPutEntry(serviceRequest));
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
    // TODO use SDOH profiles.
    // Logica does not support profiles. This blocks subsequent requests.
    //    procedure.getMeta()
    //        .addProfile(SDOHProfiles.PROCEDURE);
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
    taskOutput.setType(new CodeableConcept().addCoding(new Coding(
        "http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes", "resulting-activity",
        "Resulting Activity")));
    taskOutput.setValue(value);
    return taskOutput;
  }
}