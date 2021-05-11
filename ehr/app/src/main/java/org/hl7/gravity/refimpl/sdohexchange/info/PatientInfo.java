package org.hl7.gravity.refimpl.sdohexchange.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mykhailo Stefantsiv
 */
@AllArgsConstructor
@Getter
public class PatientInfo {
  private Patient patient;
  private Observation employmentObservation;
  private Observation education;
  //Payor can be any of Organization, Patient, RelatedPerson
  private List<IBaseResource> payors = new ArrayList<>();
}
