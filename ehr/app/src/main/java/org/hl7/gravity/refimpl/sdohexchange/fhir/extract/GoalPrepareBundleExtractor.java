package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.gravity.refimpl.sdohexchange.exception.PrepareBundleException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.GoalPrepareBundleExtractor.GoalPrepareInfoHolder;

import java.util.List;
import java.util.Map;

public class GoalPrepareBundleExtractor extends BundleExtractor<GoalPrepareInfoHolder> {

  @Override
  public GoalPrepareBundleExtractor.GoalPrepareInfoHolder extract(Bundle bundle) {
    return new GoalPrepareBundleExtractor.GoalPrepareInfoHolder(extractToMap(bundle));
  }

  @Getter
  public class GoalPrepareInfoHolder {

    private final Patient patient;
    private final Practitioner practitioner;
    private final List<Condition> problems;

    public GoalPrepareInfoHolder(Map<? extends Class<? extends Resource>, List<Resource>> resources) {
      this.patient = resourceList(resources, Patient.class).stream()
          .findFirst()
          .orElseThrow(() -> new PrepareBundleException("Patient not found."));
      this.practitioner = resourceList(resources, Practitioner.class).stream()
          .findFirst()
          .orElseThrow(() -> new PrepareBundleException("Practitioner not found."));
      this.problems = resourceList(resources, Condition.class);
    }

    public List<Condition> getProblems(List<String> problemIds) {
      if (problemIds != null && problemIds.size() != getProblems().size()) {
        throw new PrepareBundleException("Problems don't exist or are not supported.");
      }
      return getProblems();
    }
  }
}