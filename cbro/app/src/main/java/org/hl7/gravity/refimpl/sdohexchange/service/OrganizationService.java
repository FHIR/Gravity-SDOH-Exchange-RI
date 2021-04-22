package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrganizationService {

  private final IGenericClient openCbroClient;

  public List<Organization> find(String system, String code) {
    Bundle bundle = openCbroClient.search()
        .forResource(Organization.class)
        .where(Organization.IDENTIFIER.exactly()
            .systemAndCode(system, code))
        .returnBundle(Bundle.class)
        .execute();
    return FhirUtil.getFromBundle(bundle, Organization.class);
  }
}
