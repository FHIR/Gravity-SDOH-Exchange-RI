package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OccurrenceResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ServiceRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class ServiceRequestToDtoConverter implements Converter<ServiceRequest, ServiceRequestDto> {

  private final TypeToDtoConverter typeToDtoConverter = new TypeToDtoConverter();

  @Override
  public ServiceRequestDto convert(ServiceRequest serviceRequest) {
    String id = serviceRequest.getIdElement()
        .getIdPart();
    ServiceRequestDto serviceRequestDto = new ServiceRequestDto(id);
    Coding categoryCode = serviceRequest.getCategoryFirstRep()
        .getCodingFirstRep();
    serviceRequestDto.setCategory(new CodingDto(categoryCode.getCode(), categoryCode.getDisplay()));
    Coding requestCode = serviceRequest.getCode()
        .getCodingFirstRep();
    serviceRequestDto.setCode(new CodingDto(requestCode.getCode(), requestCode.getDisplay()));
    serviceRequestDto.setOccurrence(convertOccurrence(serviceRequest.getOccurrence()));

    serviceRequestDto.setConditions(serviceRequest.getReasonReference()
        .stream()
        .map(typeToDtoConverter::convert)
        .collect(Collectors.toList()));
    serviceRequestDto.setGoals(serviceRequest.getSupportingInfo()
        .stream()
        .filter(info -> info.getReferenceElement()
            .getResourceType()
            .equals(Goal.class.getSimpleName()))
        .map(typeToDtoConverter::convert)
        .collect(Collectors.toList()));
    //TODO: confirm display
    serviceRequestDto.setConsent(serviceRequest.getSupportingInfo()
        .stream()
        .filter(info -> info.getReferenceElement()
            .getResourceType()
            .equals(Consent.class.getSimpleName()))
        .map(typeToDtoConverter::convert)
        .findAny()
        .orElse(null));
    return serviceRequestDto;
  }

  private OccurrenceResponseDto convertOccurrence(Type occurrence) {
    OccurrenceResponseDto occurrenceResponseDto = null;
    if (occurrence instanceof DateTimeType) {
      occurrenceResponseDto = new OccurrenceResponseDto(null, FhirUtil.toLocalDateTime((DateTimeType) occurrence));
    } else if (occurrence instanceof Period) {
      occurrenceResponseDto = new OccurrenceResponseDto(
          FhirUtil.toLocalDateTime(((Period) occurrence).getStartElement()),
          FhirUtil.toLocalDateTime(((Period) occurrence).getEndElement()));
    }
    return occurrenceResponseDto;
  }

}