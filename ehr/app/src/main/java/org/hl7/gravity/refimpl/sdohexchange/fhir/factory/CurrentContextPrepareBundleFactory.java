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
 * Create a transaction bundle to retrieve the resources related to a current context - Patient and Practitioner.
 */
@AllArgsConstructor
public class CurrentContextPrepareBundleFactory extends PrepareBundleFactory {

  protected final String patient;
  protected final String practitioner;

  public Bundle createPrepareBundle() {
    Assert.notNull(patient, "Patient cannot be null.");
    Assert.notNull(practitioner, "Practitioner cannot be null.");

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