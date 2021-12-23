package org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask;

import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.exception.PrepareBundleException;

import java.util.List;
import java.util.Map;

/**
 * Transaction bundle parser of resources required for Patient make-contact Task creation.
 */
public class PatientMakeContactTaskPrepareBundleExtractor extends
    PatientTaskPrepareBundleExtractor<PatientMakeContactTaskPrepareBundleExtractor.PatientMakeContactTaskPrepareInfoHolder> {

  @Override
  public PatientMakeContactTaskPrepareInfoHolder extract(Bundle bundle) {
    return new PatientMakeContactTaskPrepareInfoHolder(extractToMap(bundle));
  }

  @Getter
  public class PatientMakeContactTaskPrepareInfoHolder
      extends PatientTaskPrepareBundleExtractor.PatientTaskPrepareInfoHolder {

    private final HealthcareService healthcareService;
    private final Task referralTask;

    public PatientMakeContactTaskPrepareInfoHolder(Map<? extends Class<? extends Resource>, List<Resource>> resources) {
      super(resources);
      this.healthcareService = resourceList(resources, HealthcareService.class).stream()
          .findFirst()
          .orElseThrow(() -> new PrepareBundleException("HealthcareService not found."));
      this.referralTask = resourceList(resources, Task.class).stream()
          .findFirst()
          .orElseThrow(() -> new PrepareBundleException("Task not found."));
    }
  }
}