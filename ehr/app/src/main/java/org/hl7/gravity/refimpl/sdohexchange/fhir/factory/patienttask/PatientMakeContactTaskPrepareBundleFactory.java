package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

/**
 * Create a transaction bundle to retrieve all necessary resources for Patient make-contact Task creation.
 */
public class PatientMakeContactTaskPrepareBundleFactory extends PatientTaskPrepareBundleFactory {

  private final String healthcareService;
  private final String referralTask;

  public PatientMakeContactTaskPrepareBundleFactory(String patient, String practitioner, String healthcareService,
      String referralTask) {
    super(patient, practitioner);
    this.healthcareService = healthcareService;
    this.referralTask = referralTask;
  }

  @Override
  public Bundle createPrepareBundle() {
    Bundle prepareBundle = super.createPrepareBundle();
    prepareBundle.addEntry(getHealthcareService());
    prepareBundle.addEntry(getReferralTask());
    return prepareBundle;
  }

  /**
   * Create GET entry to retrieve a HealthcareService by id.
   *
   * @return Get HealthcareService entry
   */
  protected BundleEntryComponent getHealthcareService() {
    Assert.notNull(healthcareService, "HealthcareService id can't be null.");
    return FhirUtil.createGetEntry(path(HealthcareService.class.getSimpleName(), healthcareService));
  }

  /**
   * Create GET entry to retrieve a Task by id.
   *
   * @return Get Task entry
   */
  protected BundleEntryComponent getReferralTask() {
    Assert.notNull(referralTask, "Task id can't be null.");
    return FhirUtil.createGetEntry(path(Task.class.getSimpleName(), referralTask));
  }
}