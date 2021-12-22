package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.TaskUpdateException;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class OurTaskUpdateBundleFactory {

  private static final Map<TaskStatus, List<TaskStatus>> TASK_STATE_MACHINE = new HashMap<>();

  private Task task;
  private ServiceRequest serviceRequest;
  private TaskStatus status;
  private String statusReason;
  private String comment;
  private String outcome;
  private UserDto user;

  static {
    TASK_STATE_MACHINE.put(TaskStatus.RECEIVED, Collections.singletonList(TaskStatus.CANCELLED));
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
      if (status == Task.TaskStatus.CANCELLED) {
        Assert.notNull(statusReason, "Status reason cannot be null.");
        task.setStatusReason(new CodeableConcept().setText(statusReason));
        serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.REVOKED);
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
}
