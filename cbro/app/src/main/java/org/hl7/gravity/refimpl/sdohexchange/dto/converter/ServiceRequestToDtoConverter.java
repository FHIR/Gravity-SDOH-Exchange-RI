package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHDomainCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ServiceRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class ServiceRequestToDtoConverter implements Converter<ServiceRequest, ServiceRequestDto> {

  @Override
  public ServiceRequestDto convert(ServiceRequest serviceRequest) {
    String serviceRequestId = serviceRequest.getIdElement()
        .getIdPart();
    ServiceRequestDto serviceRequestDto = new ServiceRequestDto();
    serviceRequestDto.setId(serviceRequestId);
    Coding coding = FhirUtil.findCoding(serviceRequest.getCategory(), SDOHDomainCode.SYSTEM);
    serviceRequestDto.setCategory(SDOHDomainCode.fromCode(coding.getCode())
        .getDisplay());
    return serviceRequestDto;
  }
}