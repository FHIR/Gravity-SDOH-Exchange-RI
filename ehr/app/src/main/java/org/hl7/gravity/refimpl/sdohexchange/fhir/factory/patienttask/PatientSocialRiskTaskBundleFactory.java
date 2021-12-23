package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codes.PatientTaskCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.SDCTemporaryCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.SDOHTemporaryCode;
import org.springframework.util.Assert;

/**
 * Class that holds a logic for creation of a new social-risk Patient Task with required referenced resources during a
 * "Create Task" flow. Result is a Transaction Bundle.
 */
@Getter
@Setter
public class PatientSocialRiskTaskBundleFactory extends PatientTaskBundleFactory {

  private Questionnaire questionniare;

  public Bundle createBundle() {
    Assert.notNull(questionniare, "Questionnaire cannot be null.");

    return super.createBundle();
  }

  protected Task createTask() {
    Task task = super.createTask();
    task.getCode()
        .addCoding(PatientTaskCode.COMPLETE_QUESTIONNAIRE.toCoding());
    Task.ParameterComponent input = task.addInput();
    input.getType()
        .addCoding(new Coding(SDCTemporaryCode.SYSTEM, SDCTemporaryCode.QUESTIONNAIRE.getCode(),
            SDCTemporaryCode.QUESTIONNAIRE.getDisplay()));
    input.setValue(new CanonicalType(questionniare.getUrl()));

    Task.ParameterComponent input2 = task.addInput();
    input2.getType()
        .addCoding(new Coding(SDOHTemporaryCode.SYSTEM, SDOHTemporaryCode.QUESTIONNAIRE_CATEGORY.getCode(),
            SDOHTemporaryCode.QUESTIONNAIRE_CATEGORY.getDisplay()));
    input2.setValue(new CodeableConcept().addCoding(
        new Coding(SDOHTemporaryCode.SYSTEM, SDOHTemporaryCode.RISK_QUESTIONNAIRE.getCode(),
            SDOHTemporaryCode.RISK_QUESTIONNAIRE.getDisplay())));

    return task;
  }
}
