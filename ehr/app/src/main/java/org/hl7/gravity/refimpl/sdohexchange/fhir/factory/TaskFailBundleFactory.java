package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

@Slf4j
public class TaskFailBundleFactory {

  private final Task task;
  private final ServiceRequest serviceRequest;
  private final String reason;

  public TaskFailBundleFactory(Task task, ServiceRequest serviceRequest, String reason) {
    this.task = task;
    this.serviceRequest = serviceRequest;
    this.reason = reason;
  }

  public Bundle createFailBundle() {
    Assert.notNull(task, "Task cannot be null.");
    Assert.notNull(serviceRequest, "ServiceRequest cannot be null.");
    Assert.notNull(reason, "Fail reason cannot be null.");

    Bundle failBundle = new Bundle();
    failBundle.setType(BundleType.TRANSACTION);

    task.setStatus(Task.TaskStatus.FAILED);
    task.setLastModifiedElement(DateTimeType.now());
    task.setStatusReason(new CodeableConcept().setText(reason));
    failBundle.addEntry(FhirUtil.createPutEntry(task));

    serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.REVOKED);
    failBundle.addEntry(FhirUtil.createPutEntry(serviceRequest));

    log.warn("Setting status FAILED for Task '" + task.getIdElement()
        .getIdPart() + "'. Reason: " + reason);
    return failBundle;
  }
}
