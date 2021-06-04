package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Condition;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConditionDto;
import org.springframework.core.convert.converter.Converter;

public class ConditionToDtoConverter implements Converter<Condition, ConditionDto> {

  @Override
  public ConditionDto convert(Condition condition) {
    String id = condition.getIdElement()
        .getIdPart();
    String display = condition.getCode()
        .getCodingFirstRep()
        .getDisplay();
    return new ConditionDto(id, display);
  }
}
