package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class TaskRepository extends FhirRepository<Task> {

  private final IGenericClient ehrClient;

  @Autowired
  public TaskRepository(IGenericClient ehrClient) {
    super(ehrClient);
    this.ehrClient = ehrClient;
  }

  public Bundle findByPatientId(String patientId){
    return ehrClient.search()
        .forResource(getResourceType())
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Task.PATIENT.hasId(patientId))
        //        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
        //            .value(SDOHProfiles.TASK))
        // Include ServiceRequest (other resourceTypes will be ignored)
        .include(Task.INCLUDE_FOCUS)
        // Include Organization (other resourceTypes will be ignored)
        .include(Task.INCLUDE_OWNER)
        .returnBundle(Bundle.class)
        .execute();
  }

  @Override
  public Class<Task> getResourceType() {
    return Task.class;
  }
}
