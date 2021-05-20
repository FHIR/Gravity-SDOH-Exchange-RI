package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
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

  @Override
  public ServiceRequestDto convert(ServiceRequest serviceRequest) {
    String id = serviceRequest.getIdElement()
        .getIdPart();
    ServiceRequestDto serviceRequestDto = new ServiceRequestDto();
    serviceRequestDto.setId(id);
    Coding categoryCode = serviceRequest.getCategoryFirstRep()
        .getCodingFirstRep();
    serviceRequestDto.setCategory(new CodingDto(categoryCode.getCode(), categoryCode.getDisplay()));
    Coding requestCode = serviceRequest.getCode()
        .getCodingFirstRep();
    serviceRequestDto.setCode(new CodingDto(requestCode.getCode(), requestCode.getDisplay()));
    serviceRequestDto.setOccurrence(convertOccurrence(serviceRequest.getOccurrence()));
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