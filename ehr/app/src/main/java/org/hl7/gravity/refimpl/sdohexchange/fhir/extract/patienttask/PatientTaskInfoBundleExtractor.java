package org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codes.SDCTemporaryCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.BundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskInfoBundleExtractor.PatientTaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskItemInfoBundleExtractor.PatientTaskItemInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PatientTaskInfoBundleExtractor extends BundleExtractor<List<PatientTaskInfoHolder>> {

  private final PatientTaskItemInfoBundleExtractor patientTaskItemInfoBundleExtractor =
      new PatientTaskItemInfoBundleExtractor();

  @Override
  public List<PatientTaskInfoHolder> extract(Bundle bundle) {
    List<PatientTaskItemInfoHolder> patientTaskItemInfoHolders = patientTaskItemInfoBundleExtractor.extract(bundle);
    Map<String, QuestionnaireResponse> questionnaireResponsesMap = FhirUtil.getFromBundle(bundle,
            QuestionnaireResponse.class)
        .stream()
        .collect(Collectors.toMap(qr -> qr.getIdElement()
            .getIdPart(), Function.identity()));

    return patientTaskItemInfoHolders.stream()
        .map(infoHolder -> {
          Task.TaskOutputComponent outputComponent = infoHolder.getTask()
              .getOutput()
              .stream()
              .filter(o -> FhirUtil.findCoding(Lists.newArrayList(o.getType()), SDCTemporaryCode.SYSTEM,
                  SDCTemporaryCode.QUESTIONNAIRE_RESPONSE.getCode()) != null)
              .findAny()
              .orElse(null);
          String questionnaireResponseId = null;
          if (outputComponent != null) {
            questionnaireResponseId = ((Reference) outputComponent.getValue()).getReferenceElement()
                .getIdPart();
          }
          return new PatientTaskInfoHolder(infoHolder, questionnaireResponsesMap.get(questionnaireResponseId));
        })
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
