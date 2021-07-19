package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.System;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.GoalBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.CompleteGoalRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.NewGoalDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.HealthConcernCreateException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.GoalPrepareBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.GoalBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.GoalPrepareBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.query.GoalQueryFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    Bundle responseBundle = searchGoalQuery(Goal.GoalLifecycleStatus.ACTIVE).returnBundle(Bundle.class)
        .execute();

    responseBundle = addConditionsToGoalBundle(responseBundle);
    responseBundle = addTasksAndSRsToGoalBundle(responseBundle);

    return new GoalBundleToDtoConverter().convert(responseBundle);
  }

  public List<GoalDto> listCompleted() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchGoalQuery(Goal.GoalLifecycleStatus.COMPLETED).returnBundle(Bundle.class)
        .execute();

    responseBundle = addConditionsToGoalBundle(responseBundle);
    responseBundle = addTasksAndSRsToGoalBundle(responseBundle);

    return new GoalBundleToDtoConverter().convert(responseBundle);
  }

  public GoalDto create(NewGoalDto newGoalDto, UserDto user) {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    GoalPrepareBundleFactory goalPrepareBundleFactory = new GoalPrepareBundleFactory(smartOnFhirContext.getPatient(),
        user.getId(), newGoalDto.getProblemIds());
    Bundle goalRelatedResources = ehrClient.transaction()
        .withBundle(goalPrepareBundleFactory.createPrepareBundle())
        .execute();

    GoalPrepareBundleExtractor.GoalPrepareInfoHolder goalPrepareInfoHolder = new GoalPrepareBundleExtractor().extract(
        goalRelatedResources);

    GoalBundleFactory bundleFactory = new GoalBundleFactory();
    bundleFactory.setName(newGoalDto.getName());
    String category = newGoalDto.getCategory();
    bundleFactory.setCategory(sdohMappings.findCategoryCoding(category));
    bundleFactory.setSnomedCode(
        sdohMappings.findCoding(category, Goal.class, System.SNOMED, newGoalDto.getSnomedCode()));
    bundleFactory.setAchievementStatus(newGoalDto.getAchievementStatus());
    bundleFactory.setPatient(goalPrepareInfoHolder.getPatient());
    bundleFactory.setPractitioner(goalPrepareInfoHolder.getPractitioner());
    bundleFactory.setUser(user);
    bundleFactory.setComment(newGoalDto.getComment());
    bundleFactory.setProblems(goalPrepareInfoHolder.getProblems(newGoalDto.getProblemIds()));
    bundleFactory.setStartDate(newGoalDto.getStart());

    Bundle goalCreateBundle = ehrClient.transaction()
        .withBundle(bundleFactory.createPrepareBundle())
        .execute();

    IdType goalId = FhirUtil.getFromResponseBundle(goalCreateBundle, Goal.class);
    Bundle responseBundle = searchGoalQuery(Goal.GoalLifecycleStatus.ACTIVE).where(Condition.RES_ID.exactly()
        .code(goalId.getIdPart()))
        .returnBundle(Bundle.class)
        .execute();

    Bundle merged = addConditionsToGoalBundle(responseBundle);

    return new GoalBundleToDtoConverter().convert(merged)
        .stream()
        .findFirst()
        .orElseThrow(() -> new HealthConcernCreateException("goal is not found in the response bundle."));
  }

  //TODO forbid close for goals WITH active tasks!
  public void complete(String id, CompleteGoalRequestDto dto) {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchGoalQuery(Goal.GoalLifecycleStatus.ACTIVE).where(Condition.RES_ID.exactly()
        .code(id))
        .returnBundle(Bundle.class)
        .execute();
    Goal goal = Optional.ofNullable(FhirUtil.getFirstFromBundle(responseBundle, Goal.class))
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Goal.class.getSimpleName(), id)));

    goal.setLifecycleStatus(Goal.GoalLifecycleStatus.COMPLETED);

    // Is this really an endDate?
    goal.setStatusDateElement(dto.getEnd());

    ehrClient.update()
        .resource(goal)
        .execute();
  }

  public void remove(String id) {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchGoalQuery(Goal.GoalLifecycleStatus.ACTIVE).where(Condition.RES_ID.exactly()
        .code(id))
        .returnBundle(Bundle.class)
        .execute();
    Goal goal = Optional.ofNullable(FhirUtil.getFirstFromBundle(responseBundle, Goal.class))
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Goal.class.getSimpleName(), id)));

    goal.setLifecycleStatus(Goal.GoalLifecycleStatus.CANCELLED);
    goal.setStatusDate(new Date());

    ehrClient.update()
        .resource(goal)
        .execute();
  }

  //TODO refactor. this fragmet ins used in a ProblemService as well.
  private Bundle addTasksAndSRsToGoalBundle(Bundle responseBundle) {
    Bundle tasksWithServiceRequests = ehrClient.search()
        .forResource(Task.class)
        .include(Task.INCLUDE_FOCUS)
        //Handle as much tasks as possible without pagination..
        //TODO use pagination
        .count(1000)
        .returnBundle(Bundle.class)
        .execute();

    if (tasksWithServiceRequests.getLink(IBaseBundle.LINK_NEXT) != null) {
      throw new RuntimeException(
          "Multi-page Task results returned for the List Problems operation. Pagination not supported yet. Make sure "
              + "there is a small amount of tasks in your FHIR store.");
    }

    Bundle merged = FhirUtil.mergeBundles(ehrClient.getFhirContext(), responseBundle, tasksWithServiceRequests);
    return merged;
  }

  private Bundle addConditionsToGoalBundle(Bundle responseBundle) {
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
    return merged;
  }

  private IQuery<IBaseBundle> searchGoalQuery(Goal.GoalLifecycleStatus status) {
    return new GoalQueryFactory().query(ehrClient, smartOnFhirContext.getPatient())
        .where(Goal.LIFECYCLE_STATUS.exactly()
            .code(status.toCode()))
        .sort()
        .descending(Constants.PARAM_LASTUPDATED);
  }
}