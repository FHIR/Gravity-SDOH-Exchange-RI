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
  // TODO: Temporary solution. should be queried from EHR.
  public final String TEST_PATIENT_ID = "smart-1288992";

  public ConsentRepository(IGenericClient ehrClient) {
    super(ehrClient);
  }

  public Bundle findAllByPatient(String patientId) {
    // TODO: To be removed
    patientId = TEST_PATIENT_ID;

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
