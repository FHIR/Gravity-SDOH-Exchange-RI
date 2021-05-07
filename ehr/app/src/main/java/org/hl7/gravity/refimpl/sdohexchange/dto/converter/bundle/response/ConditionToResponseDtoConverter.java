package org.hl7.gravity.refimpl.sdohexchange.dto.converter.bundle.response;

import org.hl7.fhir.r4.model.Condition;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConditionResponseDto;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Mykhailo Stefantsiv
 */
public class ConditionToResponseDtoConverter implements Converter<Condition, ConditionResponseDto> {

  @Override
  public ConditionResponseDto convert(Condition condition) {
    String id = condition.getIdElement()
        .getIdPart();
    String display = condition.getCode()
        .getText();
    return new ConditionResponseDto(id, display);
  }
}
