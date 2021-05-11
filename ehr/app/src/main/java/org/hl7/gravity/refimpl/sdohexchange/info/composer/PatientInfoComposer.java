package org.hl7.gravity.refimpl.sdohexchange.info.composer;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.CoverageRepository;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.ObservationRepository;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.PatientRepository;
import org.hl7.gravity.refimpl.sdohexchange.info.PatientInfo;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PatientInfoComposer {

  private final PatientRepository patientRepository;
  private final ObservationRepository observationRepository;
  private final CoverageRepository coverageRepository;

  public PatientInfo compose(String patientId) {
    Optional<Patient> foundPatient = patientRepository.find(patientId);
    if (!foundPatient.isPresent()) {
      throw new ResourceNotFoundException(patientId);
    }
    Patient patient = foundPatient.get();
    Observation employmentStatus = FhirUtil.getFromBundle(observationRepository.findPatientEmploymentStatus(patientId),
        Observation.class)
        .stream()
        .findFirst()
        .orElse(null);
    Bundle payorsBundle = coverageRepository.findPatientPayors(patientId);
    List<IBaseResource> payors = new ArrayList<>();
    payors.addAll(FhirUtil.getFromBundle(payorsBundle, Organization.class));
    payors.addAll(FhirUtil.getFromBundle(payorsBundle, Patient.class));
    payors.addAll(FhirUtil.getFromBundle(payorsBundle, RelatedPerson.class));

    Observation educationObservation = FhirUtil.getFromBundle(observationRepository.findPatientEducation(patientId),
        Observation.class)
        .stream()
        .findFirst()
        .orElse(null);

    return new PatientInfo(patient, employmentStatus, educationObservation, payors);
  }

}
