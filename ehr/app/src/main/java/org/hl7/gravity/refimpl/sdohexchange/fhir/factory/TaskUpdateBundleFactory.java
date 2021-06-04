package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestStatus;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class TaskUpdateBundleFactory {

  private Task task;
  private ServiceRequest serviceRequest;
  private TaskStatus status;
  private String statusReason;
  private String comment;
  private UserDto user;

  public Bundle createUpdateBundle() {
    Assert.notNull(task, "Task can't be null.");

    Bundle updateBundle = new Bundle();
    updateBundle.setType(Bundle.BundleType.TRANSACTION);

    updateTask(updateBundle);

    updateBundle.addEntry(FhirUtil.createPutEntry(task));
    return updateBundle;
  }

  private void updateTask(Bundle updateBundle) {
    if (status != null) {
      Assert.notNull(serviceRequest, "ServiceRequest can't be null.");
      Assert.notNull(statusReason, "Status reason cannot be null.");

      task.setStatus(status);
      task.setLastModifiedElement(DateTimeType.now());
      task.setStatusReason(new CodeableConcept().setText(statusReason));

      serviceRequest.setStatus(ServiceRequestStatus.REVOKED);
      updateBundle.addEntry(FhirUtil.createPutEntry(serviceRequest));
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