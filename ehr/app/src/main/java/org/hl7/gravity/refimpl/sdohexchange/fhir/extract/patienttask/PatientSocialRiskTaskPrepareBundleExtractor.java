package org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask;

import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.gravity.refimpl.sdohexchange.exception.PrepareBundleException;

import java.util.List;
import java.util.Map;

/**
 * Transaction bundle parser of resources required for Patient social-risk Task creation.
 */
public class PatientSocialRiskTaskPrepareBundleExtractor extends
    PatientTaskPrepareBundleExtractor<PatientSocialRiskTaskPrepareBundleExtractor.PatientSocialRiskTaskPrepareInfoHolder> {

  @Override
  public PatientSocialRiskTaskPrepareInfoHolder extract(Bundle bundle) {
    return new PatientSocialRiskTaskPrepareInfoHolder(extractToMap(bundle));
  }

  @Getter
  public class PatientSocialRiskTaskPrepareInfoHolder
      extends PatientTaskPrepareBundleExtractor.PatientTaskPrepareInfoHolder {

    private final Questionnaire questionnaire;

    public PatientSocialRiskTaskPrepareInfoHolder(Map<? extends Class<? extends Resource>, List<Resource>> resources) {
      super(resources);
      this.questionnaire = resourceList(resources, Questionnaire.class).stream()
          .findFirst()
          .orElseThrow(() -> new PrepareBundleException("Questionnaire not found."));
    }
  }
}