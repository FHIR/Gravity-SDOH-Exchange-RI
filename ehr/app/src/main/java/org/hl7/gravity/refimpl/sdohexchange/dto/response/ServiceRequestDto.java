package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.gravity.refimpl.sdohexchange.dto.Validated;
import org.hl7.gravity.refimpl.sdohexchange.fhir.codesystems.RequestCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.codesystems.SDOHDomainCode;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ServiceRequestDto implements Validated {

  private final String serviceRequestId;
  private ServiceRequest.ServiceRequestStatus status;
  private SDOHDomainCode category;
  private RequestCode request;
  private String details;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();
}
