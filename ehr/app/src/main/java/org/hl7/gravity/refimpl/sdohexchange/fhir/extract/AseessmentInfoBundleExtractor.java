package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Condition.ConditionEvidenceComponent;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.AseessmentInfoBundleExtractor.AssessmentInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

public class AseessmentInfoBundleExtractor extends BundleExtractor<List<AssessmentInfoHolder>> {

  @Override
  public List<AssessmentInfoHolder> extract(Bundle bundle) {
    return FhirUtil.getFromBundle(bundle, QuestionnaireResponse.class)
        .stream()
        .map(questionnaireResponse -> {
          List<Observation> observations = FhirUtil.getFromBundle(bundle, Observation.class)
              .stream()
              .filter(observation -> containsQuestionnaireReference(observation, questionnaireResponse.getIdElement()
                  .getIdPart()))
              .collect(Collectors.toList());
          List<Condition> conditions = FhirUtil.getFromBundle(bundle, Condition.class)
              .stream()
              .filter(condition -> containsObservationReference(condition, observations))
              .collect(Collectors.toList());
          return new AssessmentInfoHolder(questionnaireResponse, observations, conditions);
        })
        .collect(Collectors.toList());
  }

  private boolean containsQuestionnaireReference(Observation observation, String questionnaireId) {
    return observation.getDerivedFrom()
        .stream()
        .anyMatch(reference -> reference.getReferenceElement()
            .getResourceType()
            .equals(QuestionnaireResponse.class.getSimpleName()) && reference.getReferenceElement()
            .getIdPart()
            .equals(questionnaireId));
  }

  private boolean containsObservationReference(Condition condition, List<Observation> observations) {
    List<String> observationIds = observations.stream()
        .map(observation -> observation.getIdElement()
            .getIdPart())
        .collect(Collectors.toList());
    return condition.getEvidence()
        .stream()
        .map(ConditionEvidenceComponent::getDetail)
        .flatMap(Collection::stream)
        .anyMatch(reference -> reference.getReferenceElement()
            .getResourceType()
            .equals(Observation.class.getSimpleName()) && observationIds.contains(reference.getReferenceElement()
            .getIdPart()));
  }

  @Getter
  @AllArgsConstructor
  public static class AssessmentInfoHolder implements Comparable<AssessmentInfoHolder> {

    private final QuestionnaireResponse questionnaireResponse;
    private final List<Observation> observations;
    private final List<Condition> conditions;

    @Override
    public int compareTo(AssessmentInfoHolder other) {
      return other.questionnaireResponse.getAuthored()
          .compareTo(this.getQuestionnaireResponse()
              .getAuthored());
    }
  }
}