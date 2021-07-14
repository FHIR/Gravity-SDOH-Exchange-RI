package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.System;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.ProblemBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.NewProblemDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ProblemDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.ProblemCreateException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.ConditionClinicalStatusCodes;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCoreConditionCategory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.CurrentContextPrepareBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.CurrentContextPrepareBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.ProblemBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ProblemService {

  private final SmartOnFhirContext smartOnFhirContext;
  private final SDOHMappings sdohMappings;
  private final IGenericClient ehrClient;

  public List<ProblemDto> listActive() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchProblemQuery(ConditionClinicalStatusCodes.ACTIVE).include(
        Condition.INCLUDE_EVIDENCE_DETAIL)
        .include(Observation.INCLUDE_DERIVED_FROM.setRecurse(true))
        .returnBundle(Bundle.class)
        .execute();

    //This is risky but in scope of RI with a very limited number of resources - this will work
    responseBundle = addTasksAndSRsToConditionBundle(responseBundle);
    responseBundle = addGoalsToConditionBundle(responseBundle);

    return new ProblemBundleToDtoConverter().convert(responseBundle);
  }

  public List<ProblemDto> listClosed() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchProblemQuery(ConditionClinicalStatusCodes.RESOLVED).include(
        Condition.INCLUDE_EVIDENCE_DETAIL)
        .include(Observation.INCLUDE_DERIVED_FROM.setRecurse(true))
        .returnBundle(Bundle.class)
        .execute();

    return new ProblemBundleToDtoConverter().convert(responseBundle);
  }

  public ProblemDto create(NewProblemDto newProblemDto, UserDto user) {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    CurrentContextPrepareBundleFactory currentContextPrepareBundleFactory = new CurrentContextPrepareBundleFactory(
        smartOnFhirContext.getPatient(), user.getId());
    Bundle problemRelatedResources = ehrClient.transaction()
        .withBundle(currentContextPrepareBundleFactory.createPrepareBundle())
        .execute();
    CurrentContextPrepareBundleExtractor.CurrentContextPrepareInfoHolder currentContextPrepareInfoHolder =
        new CurrentContextPrepareBundleExtractor().extract(problemRelatedResources);

    ProblemBundleFactory bundleFactory = new ProblemBundleFactory();
    bundleFactory.setName(newProblemDto.getName());
    bundleFactory.setBasedOnText(newProblemDto.getBasedOnText());
    bundleFactory.setStartDate(newProblemDto.getStartDate());
    String category = newProblemDto.getCategory();
    bundleFactory.setCategory(sdohMappings.findCategoryCoding(category));
    bundleFactory.setConditionType(UsCoreConditionCategory.PROBLEMLISTITEM);
    bundleFactory.setIcdCode(
        sdohMappings.findCoding(category, Condition.class, System.ICD_10, newProblemDto.getIcdCode()));
    bundleFactory.setSnomedCode(
        sdohMappings.findCoding(category, Condition.class, System.SNOMED, newProblemDto.getSnomedCode()));
    bundleFactory.setPatient(currentContextPrepareInfoHolder.getPatient());
    bundleFactory.setPractitioner(currentContextPrepareInfoHolder.getPractitioner());

    Bundle problemCreateBundle = ehrClient.transaction()
        .withBundle(bundleFactory.createBundle())
        .execute();

    IdType problemId = FhirUtil.getFromResponseBundle(problemCreateBundle, Condition.class);
    Bundle responseBundle = searchProblemQuery(ConditionClinicalStatusCodes.ACTIVE).where(Condition.RES_ID.exactly()
        .code(problemId.getIdPart()))
        .returnBundle(Bundle.class)
        .execute();
    return new ProblemBundleToDtoConverter().convert(responseBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ProblemCreateException("Problem is not found in the response bundle."));
  }

  //TODO forbid close for problems WITH active tasks!
  public void close(String id) {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchProblemQuery(ConditionClinicalStatusCodes.ACTIVE).where(Condition.RES_ID.exactly()
        .code(id))
        .returnBundle(Bundle.class)
        .execute();
    Condition problem = Optional.ofNullable(FhirUtil.getFirstFromBundle(responseBundle, Condition.class))
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Condition.class.getSimpleName(), id)));

    ConditionClinicalStatusCodes resolvedStatus = ConditionClinicalStatusCodes.RESOLVED;
    Coding coding = problem.getClinicalStatus()
        .getCodingFirstRep();
    coding.setCode(resolvedStatus.toCode());
    coding.setDisplay(resolvedStatus.getDisplay());
    // set time of resolution
    problem.setAbatement(DateTimeType.now());

    ehrClient.update()
        .resource(problem)
        .execute();
  }

  private Bundle addTasksAndSRsToConditionBundle(Bundle responseBundle) {
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

  private Bundle addGoalsToConditionBundle(Bundle responseBundle) {
    Bundle goals = ehrClient.search()
        .forResource(Goal.class)
        //We request only the ones showed on UI
        .where(Goal.LIFECYCLE_STATUS.exactly()
            .codes(Goal.GoalLifecycleStatus.ACTIVE.toCode(), Goal.GoalLifecycleStatus.COMPLETED.toCode()))
        //Handle as much goals as possible without pagination.
        //TODO use pagination
        .count(1000)
        .returnBundle(Bundle.class)
        .execute();

    if (goals.getLink(IBaseBundle.LINK_NEXT) != null) {
      throw new RuntimeException(
          "Multi-page Goal results returned for the List Problems operation. Pagination not supported yet. Make sure "
              + "there is a small amount of goals in your FHIR store.");
    }
    Bundle merged = FhirUtil.mergeBundles(ehrClient.getFhirContext(), responseBundle, goals);
    return merged;
  }

  private IQuery<IBaseBundle> searchProblemQuery(ConditionClinicalStatusCodes status) {
    return ehrClient.search()
        .forResource(Condition.class)
        .where(Condition.PATIENT.hasId(smartOnFhirContext.getPatient()))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.CONDITION))
        .where(Condition.CLINICAL_STATUS.exactly()
            .code(status.toCode()))
        .where(Condition.CATEGORY.exactly()
            .systemAndCode(UsCoreConditionCategory.PROBLEMLISTITEM.getSystem(),
                UsCoreConditionCategory.PROBLEMLISTITEM.toCode()))
        .sort()
        .descending(Constants.PARAM_LASTUPDATED);
  }
}