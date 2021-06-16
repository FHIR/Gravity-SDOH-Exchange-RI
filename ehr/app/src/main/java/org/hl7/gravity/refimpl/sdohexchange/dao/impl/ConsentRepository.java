package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.springframework.stereotype.Component;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class ConsentRepository extends FhirRepository<Consent> {

  public ConsentRepository(IGenericClient ehrClient) {
    super(ehrClient);
  }

  public Bundle findAllByPatient(String patientId) {
    return getClient().search()
        .forResource(getResourceType())
        .where(Consent.PATIENT.hasId(patientId))
        .returnBundle(Bundle.class)
        .execute();
  }

  @Override
  public Class<Consent> getResourceType() {
    return Consent.class;
  }
}
