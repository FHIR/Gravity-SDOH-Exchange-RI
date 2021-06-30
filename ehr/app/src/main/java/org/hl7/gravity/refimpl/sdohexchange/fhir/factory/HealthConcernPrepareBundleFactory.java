package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

/**
 * Create a transaction bundle to retrieve all necessary resources for Health Concern creation.
 */
@AllArgsConstructor
public class HealthConcernPrepareBundleFactory extends PrepareBundleFactory {

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
   * Create GET entry to retrieve a Practitioner by id.
   *
   * @return practitioner entry
   */
  protected BundleEntryComponent getPractitionerRoleEntry() {
    Assert.notNull(practitioner, "Practitioner can't be null.");
    return FhirUtil.createGetEntry(path(Practitioner.class.getSimpleName(), practitioner));
  }
}