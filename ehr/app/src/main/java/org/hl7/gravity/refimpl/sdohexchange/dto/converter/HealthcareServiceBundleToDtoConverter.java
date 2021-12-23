package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.HealthcareServiceDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

public class HealthcareServiceBundleToDtoConverter implements Converter<Bundle, List<HealthcareServiceDto>> {

  private final LocationToDtoConverter locationToDtoConverter = new LocationToDtoConverter();

  @Override
  public List<HealthcareServiceDto> convert(Bundle bundle) {
    List<HealthcareService> services = FhirUtil.getFromBundle(bundle, HealthcareService.class);
    List<HealthcareServiceDto> serviceDtos = new ArrayList<>(services.size());
    for (HealthcareService s : services) {
      HealthcareServiceDto dto = new HealthcareServiceDto(s.getIdElement()
          .getIdPart());
      dto.setLocations(new ArrayList<>());
      dto.setName(s.getName());
      for (Reference ref : s.getLocation()) {
        if (!(ref.getResource() instanceof Location)) {
          dto.getErrors()
              .add(String.format("No Location resource was returned for reference %s", ref.getReference()));
        } else {
          dto.getLocations()
              .add(locationToDtoConverter.convert((Location) ref.getResource()));
        }
      }
      if (s.getLocation()
          .isEmpty()) {
        dto.getErrors()
            .add(String.format("No Location resources are present for HealthcareService %s", s.getId()));
      }
      serviceDtos.add(dto);
    }
    return serviceDtos;
  }
}
