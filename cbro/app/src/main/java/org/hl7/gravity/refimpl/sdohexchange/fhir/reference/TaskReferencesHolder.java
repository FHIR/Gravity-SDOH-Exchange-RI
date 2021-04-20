package org.hl7.gravity.refimpl.sdohexchange.fhir.reference;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ServiceRequest;

/**
 * Holds task reference resources.
 */
@Data
@AllArgsConstructor
public class TaskReferencesHolder {

  private Patient patient;
  private Organization requester;
  private ServiceRequest serviceRequest;
}