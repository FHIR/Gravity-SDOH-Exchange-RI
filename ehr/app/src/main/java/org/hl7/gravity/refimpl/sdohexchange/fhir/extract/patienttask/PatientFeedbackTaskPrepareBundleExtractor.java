package org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask;

import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.exception.PrepareBundleException;

import java.util.List;
import java.util.Map;

/**
 * Transaction bundle parser of resources required for Patient service-feedback Task creation.
 */
public class PatientFeedbackTaskPrepareBundleExtractor extends
    PatientTaskPrepareBundleExtractor<PatientFeedbackTaskPrepareBundleExtractor.PatientFeedbackTaskPrepareInfoHolder> {

  @Override
  public PatientFeedbackTaskPrepareInfoHolder extract(Bundle bundle) {
    return new PatientFeedbackTaskPrepareInfoHolder(extractToMap(bundle));
  }

  @Getter
  public class PatientFeedbackTaskPrepareInfoHolder
      extends PatientTaskPrepareBundleExtractor.PatientTaskPrepareInfoHolder {

    private final Task referralTask;

    public PatientFeedbackTaskPrepareInfoHolder(Map<? extends Class<? extends Resource>, List<Resource>> resources) {
      super(resources);
      this.referralTask = resourceList(resources, Task.class).stream()
          .findFirst()
          .orElseThrow(() -> new PrepareBundleException("Task not found."));
    }
  }
}