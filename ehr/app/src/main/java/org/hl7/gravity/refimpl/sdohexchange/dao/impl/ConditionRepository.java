package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.springframework.stereotype.Component;

@Component
public class ConditionRepository extends FhirRepository<Condition> {

  private final SDOHMappings sdohMappings;

  public ConditionRepository(IGenericClient ehrClient, SDOHMappings sdohMappings) {
    super(ehrClient);
    this.sdohMappings = sdohMappings;
  }

  public Bundle findByPatientAndCategoryCode(String patientId, String categoryCode) {
    return getClient().search()
        .forResource(getResourceType())
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Condition.PATIENT.hasId(patientId))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.CONDITION))
        .where(Condition.CATEGORY.exactly()
            .systemAndCode(sdohMappings.getSystem(), categoryCode))
        .returnBundle(Bundle.class)
        .execute();
  }

  @Override
  public Class<Condition> getResourceType() {
    return Condition.class;
  }
}
