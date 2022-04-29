package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicMethod;
import org.hl7.gravity.refimpl.sdohexchange.codes.DetailedEthnicityCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.DetailedRaceCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.EthnicityCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.RaceCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.characteristic.PersonalCharacteristicDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.characteristic.PersonalCharacteristicsInfoBundleExtractor.PersonalCharacteristicsInfoHolder;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PersonalCharacteristicsInfoHolderToDtoConverter<T extends PersonalCharacteristicsInfoHolder>
    implements Converter<T, PersonalCharacteristicDto> {

  @Override
  public PersonalCharacteristicDto convert(T infoHolder) {
    Observation obs = infoHolder.getObservation();
    Practitioner performer = infoHolder.getPerformer();
    PersonalCharacteristicDto dto = new PersonalCharacteristicDto();
    CodeableConcept method = obs.getMethod();
    CharacteristicCode type = CharacteristicCode.fromCode(obs.getCode()
        .getCodingFirstRep()
        .getCode());
    dto.setType(type);
    dto.setMethod(CharacteristicMethod.fromCode(obs.getMethod()
        .getCodingFirstRep()
        .getCode()));
    dto.setMethodDetail(method.getText());
    if (obs.getValue() instanceof CodeableConcept) {
      CodeableConcept value = (CodeableConcept) obs.getValue();
      dto.setValue(new CodingDto(value.getCodingFirstRep()
          .getCode(), value.getCodingFirstRep()
          .getDisplay()));
      dto.setValueDetail(value.getText());
    } else if (obs.hasComponent() && CharacteristicCode.ETHNICITY.equals(type)) {
      List<CodeableConcept> detailedValues = new ArrayList<>();
      obs.getComponent()
          .stream()
          .filter(c -> CharacteristicCode.SYSTEM.equals(c.getCode()
              .getCodingFirstRep()
              .getSystem()) && c.hasValueCodeableConcept())
          .map(Observation.ObservationComponentComponent::getValueCodeableConcept)
          .forEach(cc -> {
            if (DetailedEthnicityCode.CODES.containsKey(cc.getCodingFirstRep()
                .getCode())) {
              detailedValues.add(cc);
            } else {
              EthnicityCode ethnicityCode = EthnicityCode.fromCode(cc.getCodingFirstRep()
                  .getCode());
              dto.setValue(new CodingDto(ethnicityCode.getCode(), ethnicityCode.getDisplay()));
            }
          });
      dto.setDetailedValues(detailedValues.stream()
          .map(v -> {
            Coding coding = v.getCodingFirstRep();
            return new CodingDto(coding.getCode(), coding.getDisplay());
          })
          .collect(Collectors.toList()));
    } else if (obs.hasComponent() && CharacteristicCode.RACE.equals(type)) {
      List<CodeableConcept> detailedValues = new ArrayList<>();
      List<CodeableConcept> values = new ArrayList<>();
      obs.getComponent()
          .stream()
          .filter(c -> CharacteristicCode.SYSTEM.equals(c.getCode()
              .getCodingFirstRep()
              .getSystem()) && c.hasValueCodeableConcept())
          .map(Observation.ObservationComponentComponent::getValueCodeableConcept)
          .forEach(cc -> {
            if (DetailedRaceCode.CODES.containsKey(cc.getCodingFirstRep()
                .getCode())) {
              detailedValues.add(cc);
            } else {
              RaceCode.fromCode(cc.getCodingFirstRep()
                  .getCode());
              values.add(cc);
            }
          });
      dto.setDetailedValues(detailedValues.stream()
          .map(v -> {
            Coding coding = v.getCodingFirstRep();
            return new CodingDto(coding.getCode(), coding.getDisplay());
          })
          .collect(Collectors.toList()));
      dto.setValues(values.stream()
          .map(v -> {
            Coding coding = v.getCodingFirstRep();
            return new CodingDto(coding.getCode(), coding.getDisplay());
          })
          .collect(Collectors.toList()));
    }
    dto.setDescription(obs.getComponent()
        .stream()
        .filter(c -> CharacteristicCode.SYSTEM.equals(c.getCode()
            .getCodingFirstRep()
            .getSystem()) && c.hasValueStringType())
        .map(cc -> cc.getValueStringType()
            .getValue())
        .findFirst()
        .orElse(null));
    dto.setPerformer(new ReferenceDto(performer.getIdElement()
        .getIdPart(), performer.getNameFirstRep()
        .getNameAsSingleString()));

    return dto;
  }
}
