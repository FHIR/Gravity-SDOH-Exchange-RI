package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.gravity.refimpl.sdohexchange.codes.LoincCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.SDOHTemporaryCode;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.stereotype.Component;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class ObservationRepository extends FhirRepository<Observation> {

  public ObservationRepository(IGenericClient ehrClient) {
    super(ehrClient);
  }

  public Bundle findPatientEmploymentStatus(String patientId) {
    return getClient().search()
        .forResource(getResourceType())
        .where(Observation.PATIENT.hasId(patientId))
        .where(Observation.CATEGORY.exactly()
            .code(SDOHTemporaryCode.EMPLOYMENT_STATUS.getCode()))
        .and(Observation.CATEGORY.hasSystemWithAnyCode(SDOHTemporaryCode.SYSTEM))
        .returnBundle(Bundle.class)
        .execute();
  }

  public Bundle findPatientEducationLevel(String patientId) {
    return getClient().search()
        .forResource(getResourceType())
        .where(Observation.PATIENT.hasId(patientId))
        .where(Observation.CODE.exactly()
            .code(LoincCode.HIGHEST_EDUCATION_LEVEL.getCode()))
        .and(Observation.CODE.hasSystemWithAnyCode(LoincCode.SYSTEM))
        .returnBundle(Bundle.class)
        .execute();
  }

  // DocumentReference is included!
  public Observation getWithDocumentReference(String observationId) {
    Bundle b = getClient().search()
        .forResource(getResourceType())
        .where(Observation.RES_ID.exactly()
            .code(observationId))
        .include(Observation.INCLUDE_DERIVED_FROM)
        .returnBundle(Bundle.class)
        .execute();
    return FhirUtil.getFromBundle(b, Observation.class)
        .stream()
        .findFirst()
        .orElse(null);
  }

  public Bundle findPatientPersonalCharacteristics(String patientId, Integer count) {
    return getClient().search()
        .forResource(getResourceType())
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Observation.PATIENT.hasId(patientId))
        .where(Observation.CATEGORY.exactly()
            .code(SDOHTemporaryCode.PERSONAL_CHARACTERISTIC.getCode()))
        .include(Observation.INCLUDE_PERFORMER)
        .count(count == null ? 0 : count)
        .returnBundle(Bundle.class)
        .execute();
  }

  @Override
  public Class<Observation> getResourceType() {
    return Observation.class;
  }
}
