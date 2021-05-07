package org.hl7.gravity.refimpl.sdohexchange.dto.converter.bundle.response;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConditionResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mykhailo Stefantsiv
 */
public class ConditionBundleToResponseDtoConverter implements Converter<Bundle, List<ConditionResponseDto>> {

  @Override
  public List<ConditionResponseDto> convert(Bundle bundle) {
    return FhirUtil.getFromBundle(bundle, Condition.class)
        .stream()
        .map(this::composeConditionDto)
        .collect(Collectors.toList());
  }

  protected ConditionResponseDto composeConditionDto(Condition condition) {
    String id = condition.getIdElement()
        .getIdPart();
    String display = condition.getCode()
        .getText();
    return new ConditionResponseDto(id, display);
  }
}
