package org.hl7.gravity.refimpl.sdohexchange.info;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;

/**
 * @author Mykhailo Stefantsiv
 */
@AllArgsConstructor
@Getter
public class PatientInfo {

  private final Patient patient;
  private final Observation employment;
  private final Observation education;
  //TODO: confirm
  //Payor can be any of Organization, Patient, RelatedPerson
  private final List<IBaseResource> payors;
}