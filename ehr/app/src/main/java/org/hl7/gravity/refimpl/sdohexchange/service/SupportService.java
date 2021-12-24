package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.google.common.base.Strings;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r5.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codes.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.ConditionToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.GoalToInfoDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.HealthcareServiceBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.OrganizationToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ActiveResourcesDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConditionDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalInfoDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.HealthcareServiceDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OrganizationDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.ConditionClinicalStatusCodes;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCoreConditionCategory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.query.GoalQueryFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.query.HealthConcernQueryFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.query.ProblemQueryFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.query.TaskQueryFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SupportService {

  private final SmartOnFhirContext smartOnFhirContext;
  private final IGenericClient ehrClient;

  public List<ConditionDto> listProblems() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");
    Bundle conditionsBundle = ehrClient.search()
        .forResource(Condition.class)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Condition.PATIENT.hasId(smartOnFhirContext.getPatient()))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.CONDITION))
        .where(Condition.CLINICAL_STATUS.exactly()
            .code(ConditionClinicalStatusCodes.ACTIVE.toCode()))
        .where(Condition.CATEGORY.exactly()
            .systemAndCode(UsCoreConditionCategory.PROBLEMLISTITEM.getSystem(),
                UsCoreConditionCategory.PROBLEMLISTITEM.toCode()))
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
        .where(Goal.LIFECYCLE_STATUS.exactly()
            .code(Goal.GoalLifecycleStatus.ACTIVE.toCode()))
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

  public List<HealthcareServiceDto> listHealthcareServices(String organizationId) {
    Bundle servicesBundle = ehrClient.search()
        .forResource(HealthcareService.class)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(HealthcareService.ORGANIZATION.hasId(organizationId))
        .include(HealthcareService.INCLUDE_LOCATION)
        .returnBundle(Bundle.class)
        .execute();
    return new HealthcareServiceBundleToDtoConverter().convert(servicesBundle);
  }

  public List<ReferenceDto> listAssessments() {
    Bundle bundle = ehrClient.search()
        .forResource(Questionnaire.class)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .returnBundle(Bundle.class)
        .execute();
    return FhirUtil.getFromBundle(bundle, Questionnaire.class)
        .stream()
        .map(q -> new ReferenceDto(q.getIdElement()
            .getIdPart(), Strings.isNullOrEmpty(q.getTitle()) ? q.getName() : q.getTitle()))
        .collect(Collectors.toList());
  }

  //TODO do this in parallel.
  public ActiveResourcesDto getActiveResources() {
    IQuery hcQuery = new HealthConcernQueryFactory().query(ehrClient, smartOnFhirContext.getPatient())
        .where(Condition.CLINICAL_STATUS.exactly()
            .code(ConditionClinicalStatusCodes.ACTIVE.toCode()));

    IQuery problemQuery = new ProblemQueryFactory().query(ehrClient, smartOnFhirContext.getPatient())
        .where(Condition.CLINICAL_STATUS.exactly()
            .code(ConditionClinicalStatusCodes.ACTIVE.toCode()));

    IQuery goalQuery = new GoalQueryFactory().query(ehrClient, smartOnFhirContext.getPatient())
        .where(Goal.LIFECYCLE_STATUS.exactly()
            .code(Goal.GoalLifecycleStatus.ACTIVE.toCode()));

    IQuery taskQuery = new TaskQueryFactory().query(ehrClient, smartOnFhirContext.getPatient())
        .where(Task.STATUS.exactly()
            .codes(Task.TaskStatus.ACCEPTED.toCode(), Task.TaskStatus.DRAFT.toCode(),
                Task.TaskStatus.INPROGRESS.toCode(), Task.TaskStatus.ONHOLD.toCode(), Task.TaskStatus.READY.toCode(),
                Task.TaskStatus.RECEIVED.toCode(), Task.TaskStatus.REQUESTED.toCode()));

    return new ActiveResourcesDto(getTotal(hcQuery), getTotal(problemQuery), getTotal(goalQuery), getTotal(taskQuery));
  }

  private int getTotal(IQuery<IBaseBundle> query) {
    return query.count(0)
        .returnBundle(Bundle.class)
        .execute()
        .getTotal();
  }
}