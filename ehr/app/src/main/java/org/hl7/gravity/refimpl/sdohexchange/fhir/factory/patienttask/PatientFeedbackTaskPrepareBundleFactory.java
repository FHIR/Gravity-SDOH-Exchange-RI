package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

/**
 * Create a transaction bundle to retrieve all necessary resources for Patient service-feedback Task creation.
 */
public class PatientFeedbackTaskPrepareBundleFactory extends PatientTaskPrepareBundleFactory {

  private final String referralTask;

  public PatientFeedbackTaskPrepareBundleFactory(String patient, String practitioner, String referralTask) {
    super(patient, practitioner);
    this.referralTask = referralTask;
  }

  @Override
  public Bundle createPrepareBundle() {
    Bundle prepareBundle = super.createPrepareBundle();
    prepareBundle.addEntry(getReferralTask());
    return prepareBundle;
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