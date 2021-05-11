package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.springframework.stereotype.Component;

/**
 * @author Mykhailo Stefantsiv
 */

@Component
public class OrganizationRepository extends FhirRepository<Organization> {

  public OrganizationRepository(IGenericClient ehrClient) {
    super(ehrClient);
  }

  @Override
  public Class<Organization> getResourceType() {
    return Organization.class;
  }
}
