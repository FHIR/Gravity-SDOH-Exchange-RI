package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.codesystems.GoalStatus;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.GoalBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class GoalService {

  private final SmartOnFhirContext smartOnFhirContext;
  private final SDOHMappings sdohMappings;
  private final IGenericClient ehrClient;

  public List<GoalDto> listActive() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchGoalQuery(GoalStatus.ACTIVE).returnBundle(Bundle.class)
        .execute();

    // Extract all 'addresses' references as ids and search for corresponding Conditions, since they cannot be included.
    List<String> ids = FhirUtil.getFromBundle(responseBundle, Goal.class)
        .stream()
        .map(g -> g.getAddresses())
        .flatMap(List::stream)
        .filter(r -> Condition.class.getSimpleName()
            .equals(r.getReferenceElement()
                .getResourceType()))
        .map(r -> r.getReferenceElement()
            .getIdPart())
        .distinct()
        .collect(Collectors.toList());

    Bundle conditions = ehrClient.search()
        .forResource(Condition.class)
        .where(Condition.RES_ID.exactly()
            .codes(ids))
        .returnBundle(Bundle.class)
        .execute();

    Bundle merged = FhirUtil.mergeBundles(ehrClient.getFhirContext(), responseBundle, conditions);

    return new GoalBundleToDtoConverter().convert(merged);
  }

  private IQuery<IBaseBundle> searchGoalQuery(GoalStatus status) {
    return ehrClient.search()
        .forResource(Goal.class)
        .where(Condition.PATIENT.hasId(smartOnFhirContext.getPatient()))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.GOAL))
        .where(Goal.LIFECYCLE_STATUS.exactly()
            .code(status.toCode()));
  }
}