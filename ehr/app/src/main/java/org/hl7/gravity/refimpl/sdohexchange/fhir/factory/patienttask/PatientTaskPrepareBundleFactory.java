package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask;

import ca.uhn.fhir.rest.api.Constants;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCoreProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.PrepareBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

/**
 * Create a transaction bundle to retrieve all necessary resources for Patient Task creation.
 */
@AllArgsConstructor
public class PatientTaskPrepareBundleFactory extends PrepareBundleFactory {

  private final String patient;
  private final String practitioner;

  public Bundle createPrepareBundle() {
    Bundle prepareBundle = new Bundle();
    prepareBundle.setType(BundleType.TRANSACTION);

    prepareBundle.addEntry(getPatientEntry());
    prepareBundle.addEntry(getPractitionerRoleEntry());

    return prepareBundle;
  }

  /**
   * Create GET entry to retrieve a Patient by id.
   *
   * @return patient entry
   */
  protected BundleEntryComponent getPatientEntry() {
    Assert.notNull(patient, "Patient can't be null.");
    return FhirUtil.createGetEntry(path(Patient.class.getSimpleName(), patient));
  }

  /**
   * Create GET entry to retrieve a PractitionerRole by Practitioner id which has US Core profile related to US Core
   * Organization.
   *
   * @return practitioner role entry
   */
  protected BundleEntryComponent getPractitionerRoleEntry() {
    Assert.notNull(practitioner, "Practitioner can't be null.");
    return FhirUtil.createGetEntry(addParams(PractitionerRole.class.getSimpleName(),
        combineParams(eq(PractitionerRole.SP_PRACTITIONER, practitioner),
            eq(Constants.PARAM_PROFILE, UsCoreProfiles.PRACTITIONER_ROLE),
            eq(resourceField(PractitionerRole.SP_ORGANIZATION, Constants.PARAM_PROFILE),
                UsCoreProfiles.ORGANIZATION))));
  }
}