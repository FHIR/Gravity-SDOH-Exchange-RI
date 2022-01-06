package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.PatientTaskInfoBundleExtractor.PatientTaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.BundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskItemInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskItemInfoBundleExtractor.PatientTaskItemInfoHolder;

import java.util.List;

public class PatientTaskInfoBundleExtractor extends BundleExtractor<List<PatientTaskInfoHolder>> {

  private final PatientTaskItemInfoBundleExtractor patientTaskItem = new PatientTaskItemInfoBundleExtractor();

  @Override
  public List<PatientTaskInfoHolder> extract(Bundle bundle) {
    List<PatientTaskItemInfoHolder> patientTaskInfoHolders = patientTaskItem.extract(bundle);
    return null;
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

  public static class PatientTaskInfoBundleExtractorException extends RuntimeException {

    public PatientTaskInfoBundleExtractorException(String message) {
      super(message);
    }
  }
}
