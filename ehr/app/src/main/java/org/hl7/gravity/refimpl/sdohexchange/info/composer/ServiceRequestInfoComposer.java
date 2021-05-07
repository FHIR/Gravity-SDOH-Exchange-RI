package org.hl7.gravity.refimpl.sdohexchange.info.composer;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.gravity.refimpl.sdohexchange.fhir.util.ReferenceUtil;
import org.hl7.gravity.refimpl.sdohexchange.info.ServiceRequestInfo;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Mykhailo Stefantsiv
 */
public class ServiceRequestInfoComposer {

  private final IGenericClient ehrClient;

  public ServiceRequestInfoComposer(IGenericClient ehrClient) {
    this.ehrClient = ehrClient;
  }

  public ServiceRequestInfo compose(ServiceRequest serviceRequest) {
    String consentId = ReferenceUtil.retrieveReferencedIds(serviceRequest.getSupportingInfo(), Consent.class).get(0);
    Consent consent = ehrClient.read()
        .resource(Consent.class)
        .withId(consentId)
        .execute();
    List<String> goals = ReferenceUtil.retrieveReferencedIds(serviceRequest.getSupportingInfo(), Goal.class);
    List<String> conditions = ReferenceUtil.retrieveReferencedIds(serviceRequest.getReasonReference(), Condition.class);
    Bundle goalsBundle = ehrClient.search()
        .forResource(Goal.class)
        .where(Goal.RES_ID.exactly()
            .codes(goals))
        .returnBundle(Bundle.class)
        .execute();
    Bundle conditionsBundle = ehrClient.search()
        .forResource(Condition.class)
        .where(Condition.RES_ID.exactly()
            .codes(conditions))
        .returnBundle(Bundle.class)
        .execute();
    List<Goal> goalsList = FhirUtil.getFromBundle(goalsBundle, Goal.class);
    List<Condition> conditionsList = FhirUtil.getFromBundle(conditionsBundle, Condition.class);
    return new ServiceRequestInfo(serviceRequest, goalsList, conditionsList, consent);
  }

}
