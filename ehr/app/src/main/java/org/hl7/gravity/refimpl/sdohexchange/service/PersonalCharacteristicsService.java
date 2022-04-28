package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.PersonalPronounsCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.characteristic.NewPersonalCharacteristicDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic.PersonalCharacteristicBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic.PersonalPronounsBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalCharacteristicsService {

  private final IGenericClient ehrClient;

  public String newPersonalCharacteristic(NewPersonalCharacteristicDto newPersonalCharacteristicDto) {
    PersonalCharacteristicBundleFactory characteristicBundleFactory = null;
    if (CharacteristicCode.PERSONAL_PRONOUNS.equals(newPersonalCharacteristicDto.getType())) {
      characteristicBundleFactory = createPersonalPronounsBundleFactory(newPersonalCharacteristicDto);
    }
    Bundle obsCreateBundle = ehrClient.transaction()
        .withBundle(characteristicBundleFactory.createBundle())
        .execute();

    return FhirUtil.getFromResponseBundle(obsCreateBundle, Observation.class)
        .getIdPart();
  }

  private PersonalCharacteristicBundleFactory createPersonalPronounsBundleFactory(NewPersonalCharacteristicDto dto) {
    PersonalPronounsBundleFactory pronounsBundleFactory = new PersonalPronounsBundleFactory(dto.getType(),
        dto.getMethod(), PersonalPronounsCode.fromCode(dto.getValue()));
    pronounsBundleFactory.setMethodDetail(dto.getMethodDetail());
    pronounsBundleFactory.setValueDetail(dto.getValueDetail());
    return pronounsBundleFactory;
  }
}
