package org.hl7.gravity.refimpl.sdohexchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.gravity.refimpl.sdohexchange.fhir.IGenericClientProvider;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrganizationService {

  private final IGenericClientProvider clientProvider;
  @Value("${ehr.identifier-system}")
  private String identifierSystem;

  public String getRequesterId() {
    Bundle requesters = clientProvider.client()
        .search()
        .forResource(Organization.class)
        .where(Organization.IDENTIFIER.hasSystemWithAnyCode(identifierSystem))
        .returnBundle(Bundle.class)
        .execute();
    List<Organization> organizations = FhirUtil.getFromBundle(requesters, Organization.class);
    if (organizations.size() == 1) {
      return organizations.get(0)
          .getIdElement()
          .getIdPart();
    }
    log.warn("More that one Organization with identifier system were found.");
    return null;
  }
}
