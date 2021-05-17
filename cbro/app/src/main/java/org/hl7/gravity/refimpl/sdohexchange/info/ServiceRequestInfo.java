package org.hl7.gravity.refimpl.sdohexchange.info;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.ServiceRequest;

@Getter
@AllArgsConstructor
public class ServiceRequestInfo {

  private final ServiceRequest serviceRequest;
  private final Consent consent;
  private final List<Goal> goals;
  private final List<Condition> conditions;
}