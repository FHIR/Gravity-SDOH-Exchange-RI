package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import ca.uhn.fhir.rest.api.Constants;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * Create a transaction bundle to retrieve all necessary resources for Goal creation.
 */
@AllArgsConstructor
public class GoalPrepareBundleFactory extends PrepareBundleFactory {

  private final String patient;
  private final String practitioner;
  private final List<String> problemIds;

  public Bundle createPrepareBundle() {
    Bundle prepareBundle = new Bundle();
    prepareBundle.setType(BundleType.TRANSACTION);

    prepareBundle.addEntry(getPatientEntry());
    prepareBundle.addEntry(getPractitionerEntry());
    Optional.ofNullable(getConditionsEntry())
        .ifPresent(prepareBundle::addEntry);

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
  protected BundleEntryComponent getPractitionerEntry() {
    Assert.notNull(practitioner, "Practitioner can't be null.");
    return FhirUtil.createGetEntry(path(Practitioner.class.getSimpleName(), practitioner));
  }

  /**
   * Create GET entry to retrieve all Conditions by ids.
   *
   * @return conditions entry
   */
  protected BundleEntryComponent getConditionsEntry() {
    if (problemIds == null || problemIds.isEmpty()) {
      return null;
    }
    return FhirUtil.createGetEntry(addParams(Condition.class.getSimpleName(),
        combineParams(eq(Condition.SP_RES_ID, String.join(",", problemIds)),
            eq(Constants.PARAM_PROFILE, SDOHProfiles.CONDITION))));
  }
}