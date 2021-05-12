package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.springframework.stereotype.Component;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class CoverageRepository extends FhirRepository<Coverage> {

  public CoverageRepository(IGenericClient ehrClient) {
    super(ehrClient);
  }

  public Bundle findPatientPayors(String patientId) {
    return getClient().search()
        .forResource(getResourceType())
        .where(Coverage.BENEFICIARY.hasId(patientId))
        .include(Coverage.INCLUDE_PAYOR)
        .returnBundle(Bundle.class)
        .execute();
  }

  @Override
  public Class<Coverage> getResourceType() {
    return Coverage.class;
  }
}
