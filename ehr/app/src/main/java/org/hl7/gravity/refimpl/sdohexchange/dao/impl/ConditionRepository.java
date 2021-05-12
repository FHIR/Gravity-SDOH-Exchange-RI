package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.springframework.stereotype.Component;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class ConditionRepository extends FhirRepository<Condition> {

  public ConditionRepository(IGenericClient ehrClient) {
    super(ehrClient);
  }

  @Override
  public Class<Condition> getResourceType() {
    return Condition.class;
  }
}
