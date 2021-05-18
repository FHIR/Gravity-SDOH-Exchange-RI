package org.hl7.gravity.refimpl.sdohexchange.service;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.codesystems.EndpointConnectionType;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.OrganizationRepository;
import org.hl7.gravity.refimpl.sdohexchange.info.OrganizationInfo;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class OrganizationService {

  private final OrganizationRepository organizationRepository;

  public OrganizationInfo getOrganizationInfo(String id) {
    Assert.notNull(id, "Organization id can't be null.");
    Bundle bundle = organizationRepository.find(id, Collections.singletonList(Organization.INCLUDE_ENDPOINT));
    if (bundle.getEntry()
        .isEmpty()) {
      throw new IllegalStateException("Organization with id '" + id + "' does not exist.");
    }
    Organization organization = FhirUtil.getFromBundle(bundle, Organization.class)
        .get(0);
    Endpoint endpoint = null;
    //TODO valdiate Organization using InstanceValidator. This will validate Organization type as well.
    Coding coding = FhirUtil.findCoding(organization.getType(), OrganizationTypeCode.SYSTEM);
    if (coding != null && OrganizationTypeCode.CP.toCode()
        .equals(coding.getCode())) {
      // Retrieve FHIR Endpoint instance
      endpoint = FhirUtil.getFromBundle(bundle, Endpoint.class)
          .stream()
          .filter(e -> e.getConnectionType()
              .getCode()
              .equals(EndpointConnectionType.HL7FHIRREST.toCode()))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException(
              String.format("CP Organization resource with id '%s' does not contain endpoint of type '%s'.",
                  organization.getIdElement()
                      .getIdPart(), EndpointConnectionType.HL7FHIRREST)));
      if (Strings.isNullOrEmpty(endpoint.getAddress())) {
        throw new IllegalStateException(
            String.format("Endpoint resource with id '%s' for a CP organization '' does not contain an address.",
                endpoint.getIdElement()
                    .getIdPart(), organization.getIdElement()
                    .getIdPart()));
      }
    }
    return new OrganizationInfo(organization, endpoint);
  }

}
