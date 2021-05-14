package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.springframework.stereotype.Component;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class PatientRepository extends FhirRepository<Patient> {

  public PatientRepository(IGenericClient ehrClient) {
    super(ehrClient);
  }

  @Override
  public Class<Patient> getResourceType() {
    return Patient.class;
  }
}
