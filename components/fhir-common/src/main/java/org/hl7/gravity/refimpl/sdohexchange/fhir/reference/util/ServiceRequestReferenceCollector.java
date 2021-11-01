package org.hl7.gravity.refimpl.sdohexchange.fhir.reference.util;

import lombok.experimental.UtilityClass;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class ServiceRequestReferenceCollector {

  public static List<Reference> getGoals(ServiceRequest serviceRequest) {
    return Optional.ofNullable(serviceRequest)
        .map(ServiceRequest::getSupportingInfo)
        .orElse(Collections.emptyList())
        .stream()
        .filter(reference -> reference.getReferenceElement()
            .getResourceType()
            .equals(Goal.class.getSimpleName()))
        .collect(Collectors.toList());
  }

  public static List<Reference> getConditions(ServiceRequest serviceRequest) {
    return Optional.ofNullable(serviceRequest)
        .map(ServiceRequest::getReasonReference)
        .orElse(Collections.emptyList())
        .stream()
        .filter(reference -> reference.getReferenceElement()
            .getResourceType()
            .equals(Condition.class.getSimpleName()))
        .collect(Collectors.toList());
  }

  public static List<Reference> getConsents(ServiceRequest serviceRequest) {
    return Optional.ofNullable(serviceRequest)
        .map(ServiceRequest::getSupportingInfo)
        .orElse(Collections.emptyList())
        .stream()
        .filter(reference -> reference.getReferenceElement()
            .getResourceType()
            .equals(Consent.class.getSimpleName()))
        .collect(Collectors.toList());
  }
}
