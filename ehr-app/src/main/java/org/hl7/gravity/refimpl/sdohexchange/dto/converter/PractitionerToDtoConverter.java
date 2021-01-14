package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.springframework.core.convert.converter.Converter;

public class PractitionerToDtoConverter implements Converter<Practitioner, UserDto> {

  @Override
  public UserDto convert(Practitioner practitioner) {
    UserDto userDto = new UserDto(practitioner.getIdElement()
        .getIdPart(), Practitioner.class.getSimpleName());
    userDto.setName(practitioner.getNameFirstRep()
        .getNameAsSingleString());
    return userDto;
  }
}