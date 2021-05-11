package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.springframework.stereotype.Component;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class ObservationRepository extends FhirRepository<Observation> {

  public ObservationRepository(IGenericClient ehrClient) {
    super(ehrClient);
  }

  public Bundle findPatientEmploymentStatus(String patientId){
    return getEhrClient().search()
        .forResource(getResourceType())
        .where(Observation.PATIENT.hasId(patientId))
        .where(Observation.CATEGORY.exactly()
            .code("employment-status"))
        .returnBundle(Bundle.class)
        .execute();
  }

  public Bundle findPatientEducation(String patientId){
    return getEhrClient().search()
        .forResource(getResourceType())
        .where(Observation.PATIENT.hasId(patientId))
        .where(Observation.CODE.exactly()
            //http://hl7.org/fhir/us/dental-data-exchange/2020Sep/Observation-Education-level-example-dental.json.html
            .code("82589-3"))
        .returnBundle(Bundle.class)
        .execute();
  }

  @Override
  public Class<Observation> getResourceType() {
    return Observation.class;
  }
}
