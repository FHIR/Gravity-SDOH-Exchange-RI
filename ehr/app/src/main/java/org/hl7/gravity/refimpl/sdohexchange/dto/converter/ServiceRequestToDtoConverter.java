package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.RequestCode;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHDomainCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ServiceRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class ServiceRequestToDtoConverter implements Converter<ServiceRequest, ServiceRequestDto> {

  @Override
  public ServiceRequestDto convert(ServiceRequest sr) {
    String srId = sr.getIdElement()
        .getIdPart();
    ServiceRequestDto srDto = new ServiceRequestDto(srId);

    srDto.setStatus(sr.getStatus());
    // TODO validate profile and other properties using InstanceValidator
    //We are interested only in a single SDOHDomainCode category. Other are ignored.
    Coding coding = FhirUtil.findCoding(sr.getCategory(), SDOHDomainCode.SYSTEM);
    if (coding == null) {
      srDto.getErrors()
          .add(String.format(
              "ServiceRequest with id '%s' has no category with system '%s' within a 'category' property. Such "
                  + "requests are not expected in this context.", srId, SDOHDomainCode.SYSTEM));
    } else {
      try {
        srDto.setCategory(SDOHDomainCode.fromCode(coding.getCode()));
      } catch (FHIRException exc) {
        srDto.getErrors()
            .add(String.format("SDOHDomainCode code '%s' cannot be resolved for ServiceRequest with id '%s'.",
                coding.getCode(), srId));
      }
    }

    srDto.setRequest(RequestCode.fromCode(sr.getCode()
        .getCodingFirstRep()
        .getCode()));
    srDto.setDetails(sr.getNoteFirstRep()
        .getText());

    return srDto;
  }
}