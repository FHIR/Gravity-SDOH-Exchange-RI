package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Condition.ConditionEvidenceComponent;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.ConditionInfoBundleExtractor.ConditionInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConditionInfoBundleExtractor extends BundleExtractor<List<? extends ConditionInfoHolder>> {

  @Override
  public List<? extends ConditionInfoHolder> extract(Bundle bundle) {
    Map<String, Observation> allObservations = FhirUtil.getFromBundle(bundle, Observation.class)
        .stream()
        .collect(Collectors.toMap(observation -> observation.getIdElement()
            .getIdPart(), Function.identity()));
    Map<String, QuestionnaireResponse> allQuestionnaireResponses = FhirUtil.getFromBundle(bundle,
        QuestionnaireResponse.class)
        .stream()
        .collect(Collectors.toMap(qr -> qr.getIdElement()
            .getIdPart(), Function.identity()));

    return FhirUtil.getFromBundle(bundle, Condition.class)
        .stream()
        .map(condition -> {
          Reference evidenceReference = condition.getEvidence()
              .stream()
              .map(ConditionEvidenceComponent::getDetail)
              .flatMap(Collection::stream)
              .findFirst()
              .orElse(null);

          QuestionnaireResponse questionnaireResponse = null;
          List<Observation> observations = new ArrayList<>();
          if (evidenceReference != null) {
            Observation evidenceObservation = allObservations.remove(evidenceReference.getReferenceElement()
                .getIdPart());
            observations.add(evidenceObservation);

            questionnaireResponse = allQuestionnaireResponses.get(findQuestionnaireResponseId(evidenceObservation));
            String questionnaireResponseId = questionnaireResponse.getIdElement()
                .getIdPart();
            Iterator<Entry<String, Observation>> iterator = allObservations.entrySet()
                .iterator();
            while (iterator.hasNext()) {
              Entry<String, Observation> observationEntry = iterator.next();
              Observation value = observationEntry.getValue();
              if (containsQuestionnaireReference(value, questionnaireResponseId)) {
                observations.add(value);
                iterator.remove();
              }
            }
          }
          return new ConditionInfoHolder(condition, questionnaireResponse, observations);
        })
        .collect(Collectors.toList());
  }

  //Search recursively through all Observations related to a Concern's evidence and find the first
  // QuestionnaireResponse.
  private String findQuestionnaireResponseId(Observation evidenceObservation) {
    for (Reference ref : evidenceObservation.getDerivedFrom()) {
      if (ref.getReferenceElement()
          .getResourceType()
          .equals(QuestionnaireResponse.class.getSimpleName())) {
        return ref.getReferenceElement()
            .getIdPart();
      } else if (ref.getReferenceElement()
          .getResourceType()
          .equals(Observation.class.getSimpleName())) {
        if (ref.getResource() == null) {
          throw new IllegalStateException("Resource '" + ref.getReference()
              + "' not found in response Bundle. Does your query contain all the includes?");
        }
        return findQuestionnaireResponseId((Observation) ref.getResource());
      }
    }
    return null;
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

  @Getter
  @AllArgsConstructor
  public static class ConditionInfoHolder {

    private final Condition condition;
    private final QuestionnaireResponse questionnaireResponse;
    //TODO we do not need observation anywhere. Remove this when confirmed
    private final List<Observation> observations;
  }
}