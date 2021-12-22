package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.patientTasks.NewMakeContactTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.patientTasks.NewPatientTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.patientTasks.NewSocialRiskTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.PatientMakeContactTaskPrepareBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.PatientSocialRiskTaskPrepareBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.PatientMakeContactTaskBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.PatientMakeContactTaskPrepareBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.PatientSocialRiskTaskBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.PatientSocialRiskTaskPrepareBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.PatientTaskBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class PatientTaskService {

  private final SmartOnFhirContext smartOnFhirContext;
  private final IGenericClient ehrClient;

  public String newTask(NewPatientTaskRequestDto taskRequest, UserDto user) {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    PatientTaskBundleFactory taskBundleFactory;
    if (taskRequest instanceof NewMakeContactTaskRequestDto) {
      taskBundleFactory = createMakeContactTaskBundleFactory(user, (NewMakeContactTaskRequestDto) taskRequest);
    } else if (taskRequest instanceof NewSocialRiskTaskRequestDto) {
      taskBundleFactory = createSocialRiskTaskBundleFactory(user, (NewSocialRiskTaskRequestDto) taskRequest);
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
}