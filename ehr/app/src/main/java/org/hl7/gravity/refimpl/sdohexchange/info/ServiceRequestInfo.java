package org.hl7.gravity.refimpl.sdohexchange.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.ServiceRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mykhailo Stefantsiv
 */
@AllArgsConstructor
@Getter
public class ServiceRequestInfo {
  private ServiceRequest serviceRequest;
  private List<Goal> goals;
  private List<Condition> conditions;
  private Consent consent;
}
