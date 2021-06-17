package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.BaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.TaskBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.NewTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.UpdateTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.TaskCreateException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor.TaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskPrepareBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskPrepareBundleExtractor.TaskPrepareInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskUpdateBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskUpdateBundleExtractor.TaskUpdateInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskFailBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskPrepareBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskUpdateBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.service.CpService.CpClientException;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class TaskService {

  private final SmartOnFhirContext smartOnFhirContext;
  private final IGenericClient ehrClient;
  private final CpService cpService;
  private final SDOHMappings sdohMappings;

  public String newTask(NewTaskRequestDto taskRequest, UserDto user) {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");
    TaskPrepareBundleFactory taskPrepareBundleFactory = new TaskPrepareBundleFactory(smartOnFhirContext.getPatient(),
        user.getId(), taskRequest.getPerformerId(), taskRequest.getConsent(), taskRequest.getConditionIds(),
        taskRequest.getGoalIds());
    Bundle taskRelatedResources = ehrClient.transaction()
        .withBundle(taskPrepareBundleFactory.createPrepareBundle())
        .execute();
    TaskPrepareInfoHolder taskPrepareInfoHolder = new TaskPrepareBundleExtractor().extract(taskRelatedResources);

    TaskBundleFactory taskBundleFactory = new TaskBundleFactory();
    taskBundleFactory.setName(taskRequest.getName());
    taskBundleFactory.setPatient(taskPrepareInfoHolder.getPatient());
    taskBundleFactory.setCategory(sdohMappings.findCategoryCoding(taskRequest.getCategory()));
    taskBundleFactory.setRequestCode(sdohMappings.findResourceCoding(ServiceRequest.class, taskRequest.getCode()));
    taskBundleFactory.setPriority(taskRequest.getPriority());
    taskBundleFactory.setOccurrence(taskRequest.getOccurrence());
    taskBundleFactory.setPerformer(taskPrepareInfoHolder.getPerformerOrganization());
    taskBundleFactory.setRequester(taskPrepareInfoHolder.getPerformer());
    taskBundleFactory.setComment(taskRequest.getComment());
    taskBundleFactory.setUser(user);
    taskBundleFactory.setConsent(taskPrepareInfoHolder.getConsent());
    taskBundleFactory.setConditions(taskPrepareInfoHolder.getConditions(taskRequest.getConditionIds()));
    taskBundleFactory.setGoals(taskPrepareInfoHolder.getGoals(taskRequest.getGoalIds()));

    Bundle taskCreateBundle = ehrClient.transaction()
        .withBundle(taskBundleFactory.createBundle())
        .execute();

    IdType taskId = FhirUtil.getFromResponseBundle(taskCreateBundle, Task.class);
    TaskInfoHolder createInfo = new TaskInfoBundleExtractor().extract(getTask(taskId.getIdPart(), Task.INCLUDE_FOCUS))
        .stream()
        .findFirst()
        .orElseThrow(() -> new TaskCreateException("Task/ServiceRequest are not found in the response bundle."));
    Task task = createInfo.getTask();
    try {
      cpService.create(task, taskPrepareInfoHolder.getEndpoint());
    } catch (CpClientException exc) {
      log.warn("Task '{}' creation failed at CP. Failing a local Task and related ServiceRequest.", task.getIdElement()
          .getIdPart(), exc);
      ehrClient.transaction()
          .withBundle(new TaskFailBundleFactory(task, createInfo.getServiceRequest(), exc.getMessage()).createFailBundle())
          .execute();
    }
    return task.getIdElement()
        .getIdPart();
  }

  public TaskDto read(String id) {
    return new TaskBundleToDtoConverter().convert(getTask(id, Task.INCLUDE_FOCUS))
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id)));
  }

  public List<TaskDto> listTasks() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle tasksBundle = ehrClient.search()
        .forResource(Task.class)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Task.PATIENT.hasId(smartOnFhirContext.getPatient()))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.TASK))
        .include(Task.INCLUDE_FOCUS)
        .returnBundle(Bundle.class)
        .execute();
    return new TaskBundleToDtoConverter().convert(tasksBundle);
  }

  public void update(String id, UpdateTaskRequestDto update, UserDto user) {
    Bundle taskBundle =
        getTask(id, Task.INCLUDE_FOCUS, Task.INCLUDE_OWNER, Organization.INCLUDE_ENDPOINT.setRecurse(true));
    TaskUpdateInfoHolder updateInfo = new TaskUpdateBundleExtractor(id).extract(taskBundle);
    Task task = updateInfo.getTask();
    ServiceRequest serviceRequest = updateInfo.getServiceRequest();

    TaskUpdateBundleFactory updateBundleFactory = new TaskUpdateBundleFactory();
    updateBundleFactory.setTask(task);
    updateBundleFactory.setServiceRequest(serviceRequest);
    updateBundleFactory.setStatus(update.getStatus());
    updateBundleFactory.setStatusReason(update.getStatusReason());
    updateBundleFactory.setComment(update.getComment());
    updateBundleFactory.setUser(user);

    ehrClient.transaction()
        .withBundle(updateBundleFactory.createUpdateBundle())
        .execute();
    try {
      cpService.sync(task, serviceRequest, updateInfo.getEndpoint());
    } catch (CpClientException exc) {
      ehrClient.transaction()
          .withBundle(new TaskFailBundleFactory(task, serviceRequest, exc.getMessage()).createFailBundle())
          .execute();
    }
  }

  private Bundle getTask(String id, Include... includes) {
    IQuery<IBaseBundle> query = ehrClient.search()
        .forResource(Task.class)
        .where(BaseResource.RES_ID.exactly()
            .codes(id));
    Arrays.stream(includes)
        .forEach(query::include);
    return query
        .returnBundle(Bundle.class)
        .execute();
  }
}