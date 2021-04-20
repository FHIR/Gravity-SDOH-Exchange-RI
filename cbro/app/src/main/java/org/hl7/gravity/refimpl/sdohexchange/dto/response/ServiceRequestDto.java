package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHDomainCode;

@Getter
@Setter
@RequiredArgsConstructor
public class ServiceRequestDto {

  private String serviceRequestId;
  private ServiceRequest.ServiceRequestStatus status;
  private SDOHDomainCode category;
}
