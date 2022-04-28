package org.hl7.gravity.refimpl.sdohexchange.fhir.extract.characteristic;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.BundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.characteristic.PersonalCharacteristicsInfoBundleExtractor.PersonalCharacteristicsInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PersonalCharacteristicsInfoBundleExtractor
    extends BundleExtractor<List<PersonalCharacteristicsInfoHolder>> {

  @Override
  public List<PersonalCharacteristicsInfoHolder> extract(Bundle bundle) {
    Map<String, Practitioner> practitionerMap = FhirUtil.getFromBundle(bundle, Practitioner.class)
        .stream()
        .collect(Collectors.toMap(q -> q.getIdElement()
            .getIdPart(), Function.identity()));
    return FhirUtil.getFromBundle(bundle, Observation.class, Bundle.SearchEntryMode.MATCH)
        .stream()
        .map(obs -> {
          String performerId = obs.getPerformer()
              .stream()
              .findFirst()
              .map(o -> o.getResource()
                  .getIdElement()
                  .getIdPart())
              .orElse(null);

          if (!Strings.isNullOrEmpty(performerId) && !practitionerMap.containsKey(performerId)) {
            String reason = String.format(
                "Cannot resolve input performer for Personal Characteristic Observation resource with id '%s'.",
                obs.getIdElement()
                    .getIdPart());
            throw new PersonalCharacteristicsInfoBundleExtractorException(reason);
          }

          return new PersonalCharacteristicsInfoHolder(obs, practitionerMap.get(performerId));
        })
        .collect(Collectors.toList());
  }

  @Getter
  @RequiredArgsConstructor
  public static class PersonalCharacteristicsInfoHolder {

    private final Observation observation;
    private final Practitioner performer;
  }

  public static class PersonalCharacteristicsInfoBundleExtractorException extends RuntimeException {

    public PersonalCharacteristicsInfoBundleExtractorException(String message) {
      super(message);
    }
  }
}
