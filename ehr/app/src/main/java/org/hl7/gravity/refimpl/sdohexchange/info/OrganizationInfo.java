package org.hl7.gravity.refimpl.sdohexchange.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Organization;

@AllArgsConstructor
@Getter
public class OrganizationInfo {
  private final Organization organization;
  private final Endpoint endpoint;
}