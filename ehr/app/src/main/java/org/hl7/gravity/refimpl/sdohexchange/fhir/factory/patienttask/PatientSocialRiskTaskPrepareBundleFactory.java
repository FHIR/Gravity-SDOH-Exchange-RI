package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

/**
 * Create a transaction bundle to retrieve all necessary resources for Patient social-risk Task creation.
 */
public class PatientSocialRiskTaskPrepareBundleFactory extends PatientTaskPrepareBundleFactory {

  private final String questionnaire;

  public PatientSocialRiskTaskPrepareBundleFactory(String patient, String practitioner, String questionnaire) {
    super(patient, practitioner);
    this.questionnaire = questionnaire;
  }

  @Override
  public Bundle createPrepareBundle() {
    Bundle prepareBundle = super.createPrepareBundle();
    prepareBundle.addEntry(getQuestionnaire());
    return prepareBundle;
  }

  /**
   * Create GET entry to retrieve a Questionnaire by id.
   *
   * @return Get Questionnaire entry
   */
  protected BundleEntryComponent getQuestionnaire() {
    Assert.notNull(questionnaire, "Questionnaire id can't be null.");
    return FhirUtil.createGetEntry(path(Questionnaire.class.getSimpleName(), questionnaire));
  }
}