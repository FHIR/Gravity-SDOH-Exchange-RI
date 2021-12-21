package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.patientTasks.NewMakeContactRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.patientTasks.NewPatientTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.PatientMakeContactTaskPrepareBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.PatientMakeContactTaskBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.PatientMakeContactTaskPrepareBundleFactory;
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

    //TODO add support for more types
    NewMakeContactRequestDto makeContactTaskRequest = (NewMakeContactRequestDto) taskRequest;
    PatientMakeContactTaskPrepareBundleFactory taskPrepareBundleFactory =
        new PatientMakeContactTaskPrepareBundleFactory(smartOnFhirContext.getPatient(), user.getId(),
            makeContactTaskRequest.getHealthcareServiceId(), makeContactTaskRequest.getReferralTaskId());
    Bundle taskRelatedResources = ehrClient.transaction()
        .withBundle(taskPrepareBundleFactory.createPrepareBundle())
        .execute();
    PatientMakeContactTaskPrepareBundleExtractor.PatientMakeContactTaskPrepareInfoHolder taskPrepareInfoHolder =
        new PatientMakeContactTaskPrepareBundleExtractor().extract(taskRelatedResources);

    PatientMakeContactTaskBundleFactory taskBundleFactory = new PatientMakeContactTaskBundleFactory();
    taskBundleFactory.setName(taskRequest.getName());
    taskBundleFactory.setPatient(taskPrepareInfoHolder.getPatient());
    taskBundleFactory.setPriority(taskRequest.getPriority());
    taskBundleFactory.setOccurrence(taskRequest.getOccurrence());
    taskBundleFactory.setRequester(taskPrepareInfoHolder.getPerformer());
    taskBundleFactory.setReferralTask(taskPrepareInfoHolder.getReferralTask());
    taskBundleFactory.setComment(taskRequest.getComment());
    taskBundleFactory.setUser(user);
    //TODO verify whether the passed HealthcareService instance is related to the task
    taskBundleFactory.setContactInfo(taskPrepareInfoHolder.getHealthcareService());

    Bundle taskCreateBundle = ehrClient.transaction()
        .withBundle(taskBundleFactory.createBundle())
        .execute();

    return FhirUtil.getFromResponseBundle(taskCreateBundle, Task.class)
        .getIdPart();
  }
}