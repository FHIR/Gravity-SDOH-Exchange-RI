package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codes.PatientTaskCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.SDOHTemporaryCode;
import org.springframework.util.Assert;

/**
 * Class that holds a logic for creation of a new make-contact Patient Task with required referenced resources during a
 * "Create Task" flow. Result is a Transaction Bundle.
 */
@Getter
@Setter
public class PatientMakeContactTaskBundleFactory extends PatientTaskBundleFactory {

  private HealthcareService contactInfo;
  private Task referralTask;

  public Bundle createBundle() {
    Assert.notNull(contactInfo, "Contact Info (HealthcareService) cannot be null.");
    Assert.notNull(referralTask, "Referral Task (Task) cannot be null.");

    return super.createBundle();
  }

  protected Task createTask() {
    Task task = super.createTask();
    task.getCode()
        .addCoding(PatientTaskCode.MAKE_CONTACT.toCoding());
    task.addPartOf(new Reference(referralTask.getIdElement()
        .toUnqualifiedVersionless()));
    Task.ParameterComponent input = task.addInput();
    input.getType()
        .addCoding(new Coding(SDOHTemporaryCode.SYSTEM, SDOHTemporaryCode.CONTACT_CODE.getCode(),
            SDOHTemporaryCode.CONTACT_CODE.getDisplay()));
    input.setValue(new Reference(contactInfo.getIdElement()
        .toUnqualifiedVersionless()));
    return task;
  }
}
