package org.hl7.gravity.refimpl.sdohexchange.dto.converter.bundle;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.codesystems.ConditionState;
import org.hl7.fhir.r4.model.codesystems.ConditionVerStatus;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHDomainCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConditionDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ConditionBundleToDtoConverter implements Converter<Bundle, List<ConditionDto>> {

  @Override
  public List<ConditionDto> convert(Bundle bundle) {

    return FhirUtil.getFromBundle(bundle, Condition.class)
        .stream()
        .map(c -> composeConditionDto(c))
        .collect(Collectors.toList());
  }

  protected ConditionDto composeConditionDto(Condition c) {
    ConditionDto conditionDto = new ConditionDto(c.getIdElement()
        .getIdPart());
    conditionDto.setClinicalStatus(ConditionState.fromCode(c.getClinicalStatus()
        .getCodingFirstRep()
        .getCode()));
    Coding coding = FhirUtil.findCoding(c.getCategory(), SDOHDomainCode.SYSTEM);
    if (coding == null) {
      conditionDto.getErrors()
          .add(String.format(
              "Condition with id '%s' has no coding with system '%s' within a 'category' property. Such conditions "
                  + "are not expected in this context.", c.getIdElement()
                  .getIdPart(), OrganizationTypeCode.SYSTEM));
    } else {
      try {
        conditionDto.setDomain(SDOHDomainCode.fromCode(coding.getCode()));
      } catch (FHIRException exc) {
        conditionDto.getErrors()
            .add(String.format("SDOHDomainCode code '%s' cannot be resolved for Condition with id '%s'.",
                coding.getCode(), c.getIdElement()
                    .getIdPart()));
      }
    }

    conditionDto.setVerificationStatus(ConditionVerStatus.fromCode(c.getVerificationStatus()
        .getCodingFirstRep()
        .getCode()));
    Optional.ofNullable(c.getRecordedDate())
        .ifPresent(d -> conditionDto.setDateRecorded(d.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()));
    return conditionDto;
  }
}
