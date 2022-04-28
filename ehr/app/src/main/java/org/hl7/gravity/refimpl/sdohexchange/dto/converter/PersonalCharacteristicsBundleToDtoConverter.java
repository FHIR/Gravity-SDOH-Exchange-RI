package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.characteristic.PersonalCharacteristicDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.characteristic.PersonalCharacteristicsInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.characteristic.PersonalCharacteristicsInfoBundleExtractor.PersonalCharacteristicsInfoHolder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class PersonalCharacteristicsBundleToDtoConverter implements Converter<Bundle, List<PersonalCharacteristicDto>> {

  private final PersonalCharacteristicsInfoHolderToDtoConverter<PersonalCharacteristicsInfoHolder>
      infoHolderToDtoConverter = new PersonalCharacteristicsInfoHolderToDtoConverter();
  private final PersonalCharacteristicsInfoBundleExtractor extractor = new PersonalCharacteristicsInfoBundleExtractor();

  @Override
  public List<PersonalCharacteristicDto> convert(Bundle bundle) {
    return extractor.extract(bundle)
        .stream()
        .map(infoHolder -> {
          PersonalCharacteristicDto personalCharacteristicDto = infoHolderToDtoConverter.convert(infoHolder);
          Assert.notNull(personalCharacteristicDto, "Personal Characteristic cannot be null.");
          return personalCharacteristicDto;
        })
        .collect(Collectors.toList());
  }
}
