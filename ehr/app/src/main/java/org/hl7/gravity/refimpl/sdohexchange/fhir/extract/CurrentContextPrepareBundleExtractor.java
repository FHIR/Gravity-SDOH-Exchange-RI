package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.gravity.refimpl.sdohexchange.exception.PrepareBundleException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.CurrentContextPrepareBundleExtractor.CurrentContextPrepareInfoHolder;

import java.util.List;
import java.util.Map;

public class CurrentContextPrepareBundleExtractor extends BundleExtractor<CurrentContextPrepareInfoHolder> {

  @Override
  public CurrentContextPrepareInfoHolder extract(Bundle bundle) {
    return new CurrentContextPrepareInfoHolder(extractToMap(bundle));
  }

  @Getter
  public class CurrentContextPrepareInfoHolder {

    private final Patient patient;
    private final Practitioner practitioner;

    public CurrentContextPrepareInfoHolder(Map<? extends Class<? extends Resource>, List<Resource>> resources) {
      this.patient = resourceList(resources, Patient.class).stream()
          .findFirst()
          .orElseThrow(() -> new PrepareBundleException("Patient not found."));
      this.practitioner = resourceList(resources, Practitioner.class).stream()
          .findFirst()
          .orElseThrow(() -> new PrepareBundleException("Practitioner not found."));
    }
  }
}