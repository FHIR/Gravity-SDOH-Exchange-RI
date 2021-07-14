package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.gravity.refimpl.sdohexchange.dto.response.HealthConcernDto;

public class HealthConcernBundleToDtoConverter extends ConditionBundleToDtoConverterBase<HealthConcernDto> {

  protected HealthConcernDto newConditionDtoImpl() {
    return new HealthConcernDto();
  }
}