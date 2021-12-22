package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codes.PatientTaskCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.SDOHTemporaryCode;
import org.springframework.util.Assert;

/**
 * Class that holds a logic for creation of a new service-feedback Patient Task with required referenced resources
 * during a "Create Task" flow. Result is a Transaction Bundle.
 */
@Getter
@Setter
public class PatientFeedbackTaskBundleFactory extends PatientTaskBundleFactory {

  private Task referralTask;

  public Bundle createBundle() {
    Assert.notNull(referralTask, "Referral Task (Task) cannot be null.");

    return super.createBundle();
  }

  protected Task createTask() {
    Task task = super.createTask();
    task.getCode()
        .addCoding(PatientTaskCode.COMPLETE_QUESTIONNAIRE.toCoding());
    task.addPartOf(new Reference(referralTask.getIdElement()
        .toUnqualifiedVersionless()));
    Task.ParameterComponent input = task.addInput();
    input.getType()
        .addCoding(new Coding(SDOHTemporaryCode.SYSTEM, SDOHTemporaryCode.QUESTIONNAIRE_CATEGORY.getCode(),
            SDOHTemporaryCode.QUESTIONNAIRE_CATEGORY.getDisplay()));
    input.setValue(new CodeableConcept().addCoding(
        new Coding(SDOHTemporaryCode.SYSTEM, SDOHTemporaryCode.FEEDBACK_QUESTIONNAIRE.getCode(),
            SDOHTemporaryCode.FEEDBACK_QUESTIONNAIRE.getDisplay())));
    return task;
  }
}
