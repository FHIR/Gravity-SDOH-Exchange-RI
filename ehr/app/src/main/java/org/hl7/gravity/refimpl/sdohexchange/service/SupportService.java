package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.ConditionToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.GoalToInfoDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.OrganizationToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConditionDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalInfoDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OrganizationDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SupportService {

  private final SmartOnFhirContext smartOnFhirContext;
  private final IGenericClient ehrClient;

  public List<ConditionDto> listConditions() {
    // TODO possibly support filtering only for problem-list-item, health-concern categories. Currently _filter is
    //  not supported by Logica sandbox.
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");
    Bundle conditionsBundle = ehrClient.search()
        .forResource(Condition.class)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Condition.PATIENT.hasId(smartOnFhirContext.getPatient()))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.CONDITION))
        .returnBundle(Bundle.class)
        .execute();
    return FhirUtil.getFromBundle(conditionsBundle, Condition.class)
        .stream()
        .map(condition -> new ConditionToDtoConverter().convert(condition))
        .collect(Collectors.toList());
  }

  public List<GoalInfoDto> listGoals() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");
    Bundle goalsBundle = ehrClient.search()
        .forResource(Goal.class)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Goal.PATIENT.hasId(smartOnFhirContext.getPatient()))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.GOAL))
        .returnBundle(Bundle.class)
        .execute();
    return FhirUtil.getFromBundle(goalsBundle, Goal.class)
        .stream()
        .map(goal -> new GoalToInfoDtoConverter().convert(goal))
        .collect(Collectors.toList());
  }

  public List<OrganizationDto> listOrganizations() {
    Bundle organizationsBundle = ehrClient.search()
        .forResource(Organization.class)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Organization.TYPE.hasSystemWithAnyCode(OrganizationTypeCode.SYSTEM))
        .returnBundle(Bundle.class)
        .execute();
    return FhirUtil.getFromBundle(organizationsBundle, Organization.class)
        .stream()
        .map(org -> new OrganizationToDtoConverter().convert(org))
        .collect(Collectors.toList());
  }
}