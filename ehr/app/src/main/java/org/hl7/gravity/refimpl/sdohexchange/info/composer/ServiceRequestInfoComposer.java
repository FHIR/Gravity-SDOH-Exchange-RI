package org.hl7.gravity.refimpl.sdohexchange.info.composer;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.ConditionRepository;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.ConsentRepository;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.GoalRepository;
import org.hl7.gravity.refimpl.sdohexchange.fhir.util.ReferenceUtil;
import org.hl7.gravity.refimpl.sdohexchange.info.ServiceRequestInfo;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ServiceRequestInfoComposer {

  private final ConditionRepository conditionRepository;
  private final GoalRepository goalRepository;
  private final ConsentRepository consentRepository;

  public ServiceRequestInfo compose(ServiceRequest serviceRequest) {
    //TODO: we can use transaction bundle to retrieve all resources in one request
    String consentId = ReferenceUtil.retrieveReferencedIds(serviceRequest.getSupportingInfo(), Consent.class)
        .get(0);
    Consent consent = consentRepository.find(consentId)
        .orElse(null);
    List<String> goals = ReferenceUtil.retrieveReferencedIds(serviceRequest.getSupportingInfo(), Goal.class);
    List<String> conditions = ReferenceUtil.retrieveReferencedIds(serviceRequest.getReasonReference(), Condition.class);
    Bundle goalsBundle = goalRepository.find(goals);
    Bundle conditionsBundle = conditionRepository.find(conditions);
    List<Goal> goalsList = FhirUtil.getFromBundle(goalsBundle, Goal.class);
    List<Condition> conditionsList = FhirUtil.getFromBundle(conditionsBundle, Condition.class);
    return new ServiceRequestInfo(serviceRequest, goalsList, conditionsList, consent);
  }
}