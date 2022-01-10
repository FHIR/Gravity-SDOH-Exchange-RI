package org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask;

import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.BundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskInfoBundleExtractor.PatientTaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskItemInfoBundleExtractor.PatientTaskItemInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.List;
import java.util.stream.Collectors;

public class PatientTaskInfoBundleExtractor extends BundleExtractor<List<PatientTaskInfoHolder>> {

  private final PatientTaskItemInfoBundleExtractor patientTaskItemInfoBundleExtractor =
      new PatientTaskItemInfoBundleExtractor();

  @Override
  public List<PatientTaskInfoHolder> extract(Bundle bundle) {
    List<PatientTaskItemInfoHolder> patientTaskInfoHolder = patientTaskItemInfoBundleExtractor.extract(bundle);
    QuestionnaireResponse questionnaireResponse = FhirUtil.getFromBundle(bundle, QuestionnaireResponse.class)
        .stream()
        .findFirst()
        .orElse(null);

    return patientTaskInfoHolder.stream()
        .map(infoHolder -> new PatientTaskInfoHolder(infoHolder, questionnaireResponse))
        .collect(Collectors.toList());

  }

  @Getter
  public static class PatientTaskInfoHolder extends PatientTaskItemInfoHolder {

    private final QuestionnaireResponse questionnaireResponse;

    public PatientTaskInfoHolder(PatientTaskItemInfoHolder patientTaskItemInfoHolder,
        QuestionnaireResponse questionnaireResponse) {
      super(patientTaskItemInfoHolder.getTask(), patientTaskItemInfoHolder.getQuestionnaire());
      this.questionnaireResponse = questionnaireResponse;
    }
  }
}
