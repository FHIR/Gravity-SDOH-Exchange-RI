package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codes.SDCTemporaryCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.PatientTaskBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.PatientTaskBundleToItemDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.UpdateTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.patienttask.NewFeedbackTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.patienttask.NewMakeContactTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.patienttask.NewPatientTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.patienttask.NewSocialRiskTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask.PatientTaskDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask.PatientTaskItemDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientFeedbackTaskPrepareBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientMakeContactTaskPrepareBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientSocialRiskTaskPrepareBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.PatientTaskUpdateBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask.PatientFeedbackTaskBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask.PatientFeedbackTaskPrepareBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask.PatientMakeContactTaskBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask.PatientMakeContactTaskPrepareBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask.PatientSocialRiskTaskBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask.PatientSocialRiskTaskPrepareBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask.PatientTaskBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.query.PatientTaskQueryFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class PatientTaskService {

  private final SmartOnFhirContext smartOnFhirContext;
  private final IGenericClient ehrClient;

  public PatientTaskDto read(String id) {
    Bundle taskBundle = new PatientTaskQueryFactory().query(ehrClient, smartOnFhirContext.getPatient())
        .include(Task.INCLUDE_PART_OF)
        .where(Task.RES_ID.exactly()
            .code(id))
        //Get only patient tasks
        .where(new TokenClientParam("owner:Patient").exactly()
            .code(smartOnFhirContext.getPatient()))
        .returnBundle(Bundle.class)
        .execute();
    taskBundle = addQuestionnairesToTaskBundle(taskBundle);
    return new PatientTaskBundleToDtoConverter().convert(taskBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id)));
  }

  public List<PatientTaskItemDto> listTasks() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle tasksBundle = new PatientTaskQueryFactory().query(ehrClient, smartOnFhirContext.getPatient())
        .include(Task.INCLUDE_PART_OF)
        //Get only patient tasks
        .where(new TokenClientParam("owner:Patient").exactly()
            .code(smartOnFhirContext.getPatient()))
        .returnBundle(Bundle.class)
        .execute();
    tasksBundle = addQuestionnairesToTaskBundle(tasksBundle);
    return new PatientTaskBundleToItemDtoConverter().convert(tasksBundle);
  }

  private Bundle addQuestionnairesToTaskBundle(Bundle responseBundle) {
    // Extract all 'addresses' references as ids and search for corresponding Conditions, since they cannot be included.
    List<String> urls = FhirUtil.getFromBundle(responseBundle, Task.class)
        .stream()
        .map(t -> t.getInput()
            .stream()
            .filter(i -> SDCTemporaryCode.QUESTIONNAIRE.getCode()
                .equals(i.getType()
                    .getCodingFirstRep()
                    .getCode()))
            .findAny()
            .orElse(null))
        .filter(Objects::nonNull)
        .filter(i -> i.getValue() instanceof CanonicalType)
        .map(i -> ((CanonicalType) i.getValue()).getValue())
        .collect(Collectors.toList());

    Bundle questionnaires = ehrClient.search()
        .forResource(Questionnaire.class)
        .where(Questionnaire.URL.matches()
            .values(urls))
        .returnBundle(Bundle.class)
        .execute();

    Bundle merged = FhirUtil.mergeBundles(ehrClient.getFhirContext(), responseBundle, questionnaires);
    return merged;
  }

  public void update(String id, UpdateTaskRequestDto update, UserDto user) {
    Task task = ehrClient.read()
        .resource(Task.class)
        .withId(id)
        .execute();
    if (task == null) {
      throw new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id));
    }
    PatientTaskUpdateBundleFactory updateBundleFactory = new PatientTaskUpdateBundleFactory();
    updateBundleFactory.setTask(task);
    updateBundleFactory.setStatus(update.getFhirStatus());
    updateBundleFactory.setStatusReason(update.getStatusReason());
    updateBundleFactory.setComment(update.getComment());
    updateBundleFactory.setUser(user);

    ehrClient.transaction()
        .withBundle(updateBundleFactory.createUpdateBundle())
        .execute();
  }

  public String newTask(NewPatientTaskRequestDto taskRequest, UserDto user) {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    PatientTaskBundleFactory taskBundleFactory;
    if (taskRequest instanceof NewMakeContactTaskRequestDto) {
      taskBundleFactory = createMakeContactTaskBundleFactory(user, (NewMakeContactTaskRequestDto) taskRequest);
    } else if (taskRequest instanceof NewSocialRiskTaskRequestDto) {
      taskBundleFactory = createSocialRiskTaskBundleFactory(user, (NewSocialRiskTaskRequestDto) taskRequest);
    } else if (taskRequest instanceof NewFeedbackTaskRequestDto) {
      taskBundleFactory = createFeedbackTaskBundleFactory(user, (NewFeedbackTaskRequestDto) taskRequest);
    } else {
      throw new IllegalArgumentException(taskRequest.getClass()
          .getSimpleName() + " instances not supported yet.");
    }

    Bundle taskCreateBundle = ehrClient.transaction()
        .withBundle(taskBundleFactory.createBundle())
        .execute();

    return FhirUtil.getFromResponseBundle(taskCreateBundle, Task.class)
        .getIdPart();
  }

  private PatientMakeContactTaskBundleFactory createMakeContactTaskBundleFactory(UserDto user,
      NewMakeContactTaskRequestDto makeContactTaskRequest) {
    PatientMakeContactTaskPrepareBundleFactory taskPrepareBundleFactory =
        new PatientMakeContactTaskPrepareBundleFactory(smartOnFhirContext.getPatient(), user.getId(),
            makeContactTaskRequest.getHealthcareServiceId(), makeContactTaskRequest.getReferralTaskId());
    Bundle taskRelatedResources = ehrClient.transaction()
        .withBundle(taskPrepareBundleFactory.createPrepareBundle())
        .execute();
    PatientMakeContactTaskPrepareBundleExtractor.PatientMakeContactTaskPrepareInfoHolder taskPrepareInfoHolder =
        new PatientMakeContactTaskPrepareBundleExtractor().extract(taskRelatedResources);

    PatientMakeContactTaskBundleFactory taskBundleFactory = new PatientMakeContactTaskBundleFactory();
    taskBundleFactory.setName(makeContactTaskRequest.getName());
    taskBundleFactory.setPatient(taskPrepareInfoHolder.getPatient());
    taskBundleFactory.setPriority(makeContactTaskRequest.getPriority());
    taskBundleFactory.setOccurrence(makeContactTaskRequest.getOccurrence());
    taskBundleFactory.setRequester(taskPrepareInfoHolder.getPerformer());
    //TODO verify whether the passed Task instance is related to the Patient
    taskBundleFactory.setReferralTask(taskPrepareInfoHolder.getReferralTask());
    taskBundleFactory.setComment(makeContactTaskRequest.getComment());
    taskBundleFactory.setUser(user);
    //TODO verify whether the passed HealthcareService instance is related to the task
    taskBundleFactory.setContactInfo(taskPrepareInfoHolder.getHealthcareService());
    return taskBundleFactory;
  }

  private PatientSocialRiskTaskBundleFactory createSocialRiskTaskBundleFactory(UserDto user,
      NewSocialRiskTaskRequestDto socialRiskTaskRequest) {
    PatientSocialRiskTaskPrepareBundleFactory taskPrepareBundleFactory = new PatientSocialRiskTaskPrepareBundleFactory(
        smartOnFhirContext.getPatient(), user.getId(), socialRiskTaskRequest.getQuestionnaireId());
    Bundle taskRelatedResources = ehrClient.transaction()
        .withBundle(taskPrepareBundleFactory.createPrepareBundle())
        .execute();
    PatientSocialRiskTaskPrepareBundleExtractor.PatientSocialRiskTaskPrepareInfoHolder taskPrepareInfoHolder =
        new PatientSocialRiskTaskPrepareBundleExtractor().extract(taskRelatedResources);

    PatientSocialRiskTaskBundleFactory taskBundleFactory = new PatientSocialRiskTaskBundleFactory();
    taskBundleFactory.setName(socialRiskTaskRequest.getName());
    taskBundleFactory.setPatient(taskPrepareInfoHolder.getPatient());
    taskBundleFactory.setPriority(socialRiskTaskRequest.getPriority());
    taskBundleFactory.setOccurrence(socialRiskTaskRequest.getOccurrence());
    taskBundleFactory.setRequester(taskPrepareInfoHolder.getPerformer());
    taskBundleFactory.setComment(socialRiskTaskRequest.getComment());
    taskBundleFactory.setUser(user);
    taskBundleFactory.setQuestionniare(taskPrepareInfoHolder.getQuestionnaire());
    return taskBundleFactory;
  }

  private PatientTaskBundleFactory createFeedbackTaskBundleFactory(UserDto user,
      NewFeedbackTaskRequestDto feedbackTaskRequest) {
    PatientFeedbackTaskPrepareBundleFactory taskPrepareBundleFactory = new PatientFeedbackTaskPrepareBundleFactory(
        smartOnFhirContext.getPatient(), user.getId(), feedbackTaskRequest.getReferralTaskId());
    Bundle taskRelatedResources = ehrClient.transaction()
        .withBundle(taskPrepareBundleFactory.createPrepareBundle())
        .execute();
    PatientFeedbackTaskPrepareBundleExtractor.PatientFeedbackTaskPrepareInfoHolder taskPrepareInfoHolder =
        new PatientFeedbackTaskPrepareBundleExtractor().extract(taskRelatedResources);

    PatientFeedbackTaskBundleFactory taskBundleFactory = new PatientFeedbackTaskBundleFactory();
    taskBundleFactory.setName(feedbackTaskRequest.getName());
    taskBundleFactory.setPatient(taskPrepareInfoHolder.getPatient());
    taskBundleFactory.setPriority(feedbackTaskRequest.getPriority());
    taskBundleFactory.setOccurrence(feedbackTaskRequest.getOccurrence());
    taskBundleFactory.setRequester(taskPrepareInfoHolder.getPerformer());
    //TODO verify whether the passed Task instance is related to the Patient
    taskBundleFactory.setReferralTask(taskPrepareInfoHolder.getReferralTask());
    taskBundleFactory.setComment(feedbackTaskRequest.getComment());
    taskBundleFactory.setUser(user);
    return taskBundleFactory;
  }
}