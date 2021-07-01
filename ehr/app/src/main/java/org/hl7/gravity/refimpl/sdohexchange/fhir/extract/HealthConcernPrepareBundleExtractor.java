package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.gravity.refimpl.sdohexchange.exception.HealthConcernPrepareException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.HealthConcernPrepareBundleExtractor.HealthConcernPrepareInfoHolder;

public class HealthConcernPrepareBundleExtractor extends BundleExtractor<HealthConcernPrepareInfoHolder> {

  @Override
  public HealthConcernPrepareBundleExtractor.HealthConcernPrepareInfoHolder extract(Bundle bundle) {
    return new HealthConcernPrepareBundleExtractor.HealthConcernPrepareInfoHolder(extractToMap(bundle));
  }

  @Getter
  public class HealthConcernPrepareInfoHolder {

    private final Patient patient;
    private final Practitioner practitioner;

    public HealthConcernPrepareInfoHolder(Map<? extends Class<? extends Resource>, List<Resource>> resources) {
      this.patient = resourceList(resources, Patient.class)
          .stream()
          .findFirst()
          .orElseThrow(() -> new HealthConcernPrepareException("Patient not found."));
      this.practitioner = resourceList(resources, Practitioner.class)
          .stream()
          .findFirst()
          .orElseThrow(() -> new HealthConcernPrepareException("Practitioner not found."));
    }
  }
}