package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Location;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.LocationDto;
import org.springframework.core.convert.converter.Converter;

public class LocationToDtoConverter implements Converter<Location, LocationDto> {

  @Override
  public LocationDto convert(Location location) {
    LocationDto locationDto = new LocationDto(location.getIdElement()
        .getIdPart());
    locationDto.setText(location.getAddress()
        .getText());
    return locationDto;
  }
}
