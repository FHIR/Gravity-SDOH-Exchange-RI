package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.springframework.stereotype.Component;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class GoalRepository extends FhirRepository<Goal> {

  public GoalRepository(IGenericClient ehrClient) {
    super(ehrClient);
  }

  @Override
  public Class<Goal> getResourceType() {
    return Goal.class;
  }
}
