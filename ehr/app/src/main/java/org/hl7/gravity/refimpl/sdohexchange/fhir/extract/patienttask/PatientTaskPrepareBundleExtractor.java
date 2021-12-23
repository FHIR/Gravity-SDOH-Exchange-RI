package org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask;

import lombok.Getter;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.gravity.refimpl.sdohexchange.exception.PrepareBundleException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.BundleExtractor;

import java.util.List;
import java.util.Map;

/**
 * Transaction bundle parser of resources required for Patient Task creation.
 */
public abstract class PatientTaskPrepareBundleExtractor<T extends PatientTaskPrepareBundleExtractor.PatientTaskPrepareInfoHolder>
    extends BundleExtractor<T> {

  @Getter
  public class PatientTaskPrepareInfoHolder {

    private final Patient patient;
    private final PractitionerRole practitionerRole;

    public PatientTaskPrepareInfoHolder(Map<? extends Class<? extends Resource>, List<Resource>> resources) {
      this.patient = resourceList(resources, Patient.class).stream()
          .findFirst()
          .orElseThrow(() -> new PrepareBundleException("Patient not found."));
      this.practitionerRole = getRole(resourceList(resources, PractitionerRole.class));
    }

    public Reference getPerformer() {
      return getPractitionerRole().getOrganization();
    }

    private PractitionerRole getRole(List<PractitionerRole> roles) {
      if (roles.isEmpty()) {
        throw new PrepareBundleException(
            "No Practitioner role with US Core profile which references to US Core Organization have been found.");
      } else if (roles.size() > 1) {
        throw new PrepareBundleException(
            "More than one Practitioner role with US Core profile which references to US Core Organization have been "
                + "found.");
      }
      return roles.stream()
          .findFirst()
          .get();
    }
  }
}