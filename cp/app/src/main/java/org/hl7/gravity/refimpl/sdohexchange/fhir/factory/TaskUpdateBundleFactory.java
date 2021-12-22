package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import lombok.Getter;
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
import org.hl7.gravity.refimpl.sdohexchange.dto.request.Priority;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.TaskUpdateException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.reference.util.ServiceRequestReferenceCollector;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class TaskUpdateBundleFactory {

  private static final Map<TaskStatus, List<TaskStatus>> TASK_STATE_MACHINE = new HashMap<>();

  private Task task;
  private ServiceRequest serviceRequest;
  private TaskStatus status;
  private String statusReason;
  private String comment;
  private String outcome;
  private List<Coding> procedureCodes;
  private UserDto user;
  private Priority priorityForCBO;
  private Reference cboTaskRequester;
  private Reference cboTaskOwner;

  static {
    TASK_STATE_MACHINE.put(TaskStatus.RECEIVED, Arrays.asList(TaskStatus.REJECTED, TaskStatus.ACCEPTED));
    TASK_STATE_MACHINE.put(TaskStatus.ACCEPTED, Collections.singletonList(TaskStatus.CANCELLED));
    TASK_STATE_MACHINE.put(TaskStatus.INPROGRESS, Collections.singletonList(TaskStatus.CANCELLED));
    TASK_STATE_MACHINE.put(TaskStatus.ONHOLD, Collections.singletonList(TaskStatus.CANCELLED));
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
    List<TaskStatus> possibleStatuses = TASK_STATE_MACHINE.get(task.getStatus());
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
      if (status == TaskStatus.ACCEPTED) {
        Assert.notNull(priorityForCBO, "Priority for CBO cannot be null.");
        Assert.notNull(cboTaskRequester, "CBO Task requester cannot be null.");

        //TODO move to OurTaskBundleFactory
        ServiceRequest cboServiceRequest = serviceRequest.copy();
        cboServiceRequest.setId(IdType.newRandomUuid());
        cboServiceRequest.getIdentifier()
            .clear();
        cboServiceRequest.setStatus(ServiceRequest.ServiceRequestStatus.ACTIVE);
        cboServiceRequest.setIntent(ServiceRequest.ServiceRequestIntent.FILLERORDER);
        cboServiceRequest.setAuthoredOnElement(DateTimeType.now());
        cboServiceRequest.setPriority(priorityForCBO.getServiceRequestPriority());
        updateBundle.addEntry(FhirUtil.createPostEntry(cboServiceRequest));

        Task cboTask = task.copy();
        cboTask.setId((String) null);
        cboTask.getIdentifier()
            .clear();
        cboTask.addBasedOn(new Reference(task.getIdElement()
            .toUnqualifiedVersionless()));
        cboTask.setStatus(TaskStatus.RECEIVED);
        cboTask.setAuthoredOnElement(DateTimeType.now());
        cboTask.setLastModifiedElement(DateTimeType.now());
        cboTask.setFocus(new Reference(cboServiceRequest));
        cboTask.setIntent(Task.TaskIntent.FILLERORDER);
        cboTask.setPriority(priorityForCBO.getTaskPriority());
        cboTask.setRequester(cboTaskRequester);
        if (cboTaskOwner != null) {
          cboTask.setOwner(cboTaskOwner);
        }
        updateBundle.addEntry(FhirUtil.createPostEntry(cboTask));
      } else if (status == TaskStatus.REJECTED || status == TaskStatus.CANCELLED) {
        Assert.notNull(statusReason, "Status reason cannot be null.");
        task.setStatusReason(new CodeableConcept().setText(statusReason));
        if (status == TaskStatus.CANCELLED) {
          createProceduresOutput(updateBundle);
        }
        serviceRequest.setStatus(ServiceRequestStatus.REVOKED);
        updateBundle.addEntry(FhirUtil.createPutEntry(serviceRequest));
      } else {
        throw new TaskUpdateException("Status " + status.getDisplay() + " cannot be set explicitly.");
      }
    }
    if (StringUtils.hasText(comment)) {
      Assert.notNull(user, "User cannot be null.");
      task.addNote()
          .setText(comment)
          .setTimeElement(DateTimeType.now())
          .setAuthor(new Reference(new IdType(user.getUserType(), user.getId())).setDisplay(user.getName()));
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