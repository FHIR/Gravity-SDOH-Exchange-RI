package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceRequestRepository extends FhirRepository<ServiceRequest> {

  @Autowired
  public ServiceRequestRepository(IGenericClient cbroClient) {
    super(cbroClient);
  }

  @Override
  public Class<ServiceRequest> getResourceType() {
    return ServiceRequest.class;
  }
}
