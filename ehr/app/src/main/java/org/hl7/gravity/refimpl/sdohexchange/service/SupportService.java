package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHDomainCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.ConditionBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.GoalBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.OrganizationToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConditionDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OrganizationDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SupportService {

  private final IGenericClient ehrClient;
  private final SmartOnFhirContext smartOnFhirContext;

  public List<ConditionDto> listConditions() {
    // TODO possibly support filtering only for problem-list-item, health-concern categories. Currently _filter is
    //  not supported by Logica sandbox.
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle bundle = ehrClient.search()
        .forResource(Condition.class)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Condition.PATIENT.hasId(smartOnFhirContext.getPatient()))
        //        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
        //            .value(SDOHProfiles.CONDITION))
        .where(Condition.CATEGORY.hasSystemWithAnyCode(SDOHDomainCode.SYSTEM))
        .returnBundle(Bundle.class)
        .execute();
    return new ConditionBundleToDtoConverter().convert(bundle);
  }

  public List<GoalDto> listGoals() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle bundle = ehrClient.search()
        .forResource(Goal.class)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Goal.PATIENT.hasId(smartOnFhirContext.getPatient()))
        //        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
        //            .value(SDOHProfiles.GOAL))
        .where(Goal.CATEGORY.hasSystemWithAnyCode(SDOHDomainCode.SYSTEM))
        .returnBundle(Bundle.class)
        .execute();
    return new GoalBundleToDtoConverter().convert(bundle);
  }

  public List<OrganizationDto> listOrganizations() {
    Bundle bundle = ehrClient.search()
        .forResource(Organization.class)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Organization.TYPE.hasSystemWithAnyCode(OrganizationTypeCode.SYSTEM))
        .returnBundle(Bundle.class)
        .execute();

    return FhirUtil.getFromBundle(bundle, Organization.class)
        .stream()
        .map(org -> new OrganizationToDtoConverter().convert(org))
        .collect(Collectors.toList());
  }
}