package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.springframework.stereotype.Component;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class ProcedureRepository extends FhirRepository<Procedure> {

  public ProcedureRepository(IGenericClient ehrClient) {
    super(ehrClient);
  }

  @Override
  public Class<Procedure> getResourceType() {
    return Procedure.class;
  }
}
