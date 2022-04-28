package org.hl7.gravity.refimpl.sdohexchange.service;

import com.google.common.collect.Lists;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.EthnicityCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.GenderIdentityCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.PersonalPronounsCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.RaceCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.SexualOrientationCode;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.ObservationRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.PersonalCharacteristicsBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.characteristic.NewPersonalCharacteristicDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.characteristic.PersonalCharacteristicDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic.EthnicityBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic.GenderIdentityBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic.PersonalCharacteristicBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic.PersonalPronounsBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic.RaceBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic.SexualOrientationBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalCharacteristicsService {

  private final ObservationRepository observationRepository;

  public List<PersonalCharacteristicDto> listPersonalCharacteristics(Integer count) {
    Bundle bundle = observationRepository.findPatientPersonalCharacteristics(SmartOnFhirContext.get()
        .getPatient(), count);
    return new PersonalCharacteristicsBundleToDtoConverter().convert(bundle);
  }

  public String newPersonalCharacteristic(NewPersonalCharacteristicDto newPersonalCharacteristicDto) {
    PersonalCharacteristicBundleFactory characteristicBundleFactory = null;
    if (CharacteristicCode.PERSONAL_PRONOUNS.equals(newPersonalCharacteristicDto.getType())) {
      characteristicBundleFactory = createPersonalPronounsBundleFactory(newPersonalCharacteristicDto);
    } else if (CharacteristicCode.RACE.equals(newPersonalCharacteristicDto.getType())) {
      characteristicBundleFactory = createRaceBundleFactory(newPersonalCharacteristicDto);
    } else if (CharacteristicCode.ETHNICITY.equals(newPersonalCharacteristicDto.getType())) {
      characteristicBundleFactory = createEthnicityBundleFactory(newPersonalCharacteristicDto);
    } else if (CharacteristicCode.SEXUAL_ORIENTATION.equals(newPersonalCharacteristicDto.getType())) {
      characteristicBundleFactory = createSexualOrientationBundleFactory(newPersonalCharacteristicDto);
    } else if (CharacteristicCode.GENDER_IDENTITY.equals(newPersonalCharacteristicDto.getType())) {
      characteristicBundleFactory = createGenderIdentityBundleFactory(newPersonalCharacteristicDto);
    }
    Bundle obsCreateBundle = observationRepository.transaction(characteristicBundleFactory.createBundle());

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

  private PersonalCharacteristicBundleFactory createSexualOrientationBundleFactory(NewPersonalCharacteristicDto dto) {
    SexualOrientationBundleFactory sexualOrientationBundleFactory = new SexualOrientationBundleFactory(dto.getType(),
        dto.getMethod(), SexualOrientationCode.fromCode(dto.getValue()));
    sexualOrientationBundleFactory.setMethodDetail(dto.getMethodDetail());
    sexualOrientationBundleFactory.setValueDetail(dto.getValueDetail());
    return sexualOrientationBundleFactory;
  }

  private GenderIdentityBundleFactory createGenderIdentityBundleFactory(NewPersonalCharacteristicDto dto) {
    GenderIdentityBundleFactory genderIdentityBundleFactory = new GenderIdentityBundleFactory(dto.getType(),
        dto.getMethod(), GenderIdentityCode.fromCode(dto.getValue()));
    genderIdentityBundleFactory.setMethodDetail(dto.getMethodDetail());
    genderIdentityBundleFactory.setValueDetail(dto.getValueDetail());
    return genderIdentityBundleFactory;
  }

  private PersonalCharacteristicBundleFactory createRaceBundleFactory(NewPersonalCharacteristicDto dto) {
    RaceBundleFactory raceBundleFactory = new RaceBundleFactory(dto.getType(), dto.getMethod());
    raceBundleFactory.setMethodDetail(dto.getMethodDetail());
    if (dto.getValues() != null) {
      raceBundleFactory.setValues(Lists.newArrayList(dto.getValues())
          .stream()
          .map(RaceCode::fromCode)
          .toArray(RaceCode[]::new));
    }
    raceBundleFactory.setDetailedValues(dto.getDetailedValues());
    raceBundleFactory.setDescription(dto.getDescription());
    return raceBundleFactory;
  }

  private PersonalCharacteristicBundleFactory createEthnicityBundleFactory(NewPersonalCharacteristicDto dto) {
    EthnicityBundleFactory raceBundleFactory = new EthnicityBundleFactory(dto.getType(), dto.getMethod());
    raceBundleFactory.setMethodDetail(dto.getMethodDetail());
    raceBundleFactory.setValue(EthnicityCode.fromCode(dto.getValue()));
    raceBundleFactory.setDetailedValues(dto.getDetailedValues());
    raceBundleFactory.setDescription(dto.getDescription());
    return raceBundleFactory;
  }
}
