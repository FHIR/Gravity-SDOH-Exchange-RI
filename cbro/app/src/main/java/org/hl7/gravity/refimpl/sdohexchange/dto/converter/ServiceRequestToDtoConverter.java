package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.RequestCode;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHDomainCode;
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
    serviceRequestDto.setCategory(serviceRequest.getCategoryFirstRep()
        .getCodingFirstRep()
        .getDisplay());
    serviceRequestDto.setRequestCode(serviceRequest.getCode()
        .getCodingFirstRep()
        .getDisplay());
    Type occurrence = serviceRequest.getOccurrence();
    if (occurrence instanceof DateTimeType) {
      serviceRequestDto.setOccurrence(
          new OccurrenceResponseDto(null, FhirUtil.toLocalDateTime((DateTimeType) occurrence)));
    } else if (occurrence instanceof Period) {
      serviceRequestDto.setOccurrence(
          new OccurrenceResponseDto(FhirUtil.toLocalDateTime(((Period) occurrence).getStartElement()),
              FhirUtil.toLocalDateTime(((Period) occurrence).getEndElement())));
    }
    return serviceRequestDto;
  }
}