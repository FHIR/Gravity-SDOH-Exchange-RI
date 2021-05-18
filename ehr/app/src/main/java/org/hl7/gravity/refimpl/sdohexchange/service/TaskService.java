package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestStatus;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.TaskRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.info.TaskInfoToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.NewTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.UpdateTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.info.OrganizationInfo;
import org.hl7.gravity.refimpl.sdohexchange.info.TaskInfo;
import org.hl7.gravity.refimpl.sdohexchange.info.composer.TasksInfoComposer;
import org.hl7.gravity.refimpl.sdohexchange.service.CpService.CpClientException;
import org.hl7.gravity.refimpl.sdohexchange.service.CpTaskCreateService.CpTaskCreateException;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class TaskService {

  private final IGenericClient ehrClient;
  private final SmartOnFhirContext smartOnFhirContext;
  private final CpService cpService;
  private final CpTaskCreateService cpTaskCreateService;
  private final CpTaskUpdateService cpTaskUpdateService;
  private final PractitionerRoleService practitionerRoleService;
  private final ContextService contextService;
  private final TaskRepository taskRepository;
  private final TasksInfoComposer tasksInfoComposer;
  private final OrganizationService organizationService;
  private final SDOHMappings sdohMappings;

  public String newTask(NewTaskRequestDto taskRequest) {
    if (taskRequest.getConsent() != Boolean.TRUE) {
      throw new IllegalStateException("Patient consent must be provided. Set consent to TRUE.");
    }
    // Create a Task Bundle
    UserDto user = contextService.getUser();
    PractitionerRole role = practitionerRoleService.getRole(user.getId());
    String requesterId = role.getOrganization()
        .getReferenceElement()
        .getIdPart();

    Coding category = sdohMappings.findCategory(taskRequest.getCategory());
    Coding requestCode = sdohMappings.findCoding(ServiceRequest.class, taskRequest.getCode());

    TaskBundleFactory taskBundleFactory = new TaskBundleFactory(taskRequest.getName(), smartOnFhirContext.getPatient(),
        category, requestCode, taskRequest.getPriority(), taskRequest.getOccurrence(), taskRequest.getPerformerId(),
        requesterId);
    taskBundleFactory.setComment(taskRequest.getComment());
    taskBundleFactory.setUser(user);

    // TODO check conditions and goals are of SDOHCC profile?
    if (taskRequest.getConditionIds() != null) {
      taskBundleFactory.getConditionIds()
          .addAll(taskRequest.getConditionIds());
    }
    if (taskRequest.getGoalIds() != null) {
      taskBundleFactory.getGoalIds()
          .addAll(taskRequest.getGoalIds());
    }
    // Verify References
    // Performer Id is set - assert is present in TaskBundleFactory.
    // Fetch it and related Endpoint in case an Organization is a CP.
    OrganizationInfo organizationInfo = organizationService.getOrganizationInfo(taskRequest.getPerformerId());

    // Store a task
    Bundle respBundle = taskRepository.transaction(taskBundleFactory.createBundle());
    IdType taskId = FhirUtil.getFromResponseBundle(respBundle, Task.class);

    Endpoint endpoint = organizationInfo.getEndpoint();
    //If endpoint!=null - this is a CP use case. Manage a task additionally.
    if (endpoint != null) {
      Optional<Task> optionalTask = taskRepository.find(taskId.getIdPart());
      optionalTask.ifPresent(task -> handleCpTask(task, endpoint));
      optionalTask.orElseThrow(() -> new ResourceNotFoundException(taskId));
    }
    return taskId.getIdPart();
  }

  public List<TaskDto> listTasks() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    TaskInfoToDtoConverter taskInfoToDtoConverter = new TaskInfoToDtoConverter();
    Bundle bundle = taskRepository.findByPatientId(smartOnFhirContext.getPatient());
    List<TaskInfo> taskInfos = tasksInfoComposer.compose(bundle);
    return taskInfos.stream()
        .map(taskInfoToDtoConverter::convert)
        .collect(Collectors.toList());
  }

  //TODO: Refactor
  public TaskDto update(String id, UpdateTaskRequestDto update) {
    Bundle taskBundle = taskRepository.find(id, Arrays.asList(Task.INCLUDE_FOCUS, Task.INCLUDE_OWNER));
    List<TaskInfo> taskInfos = tasksInfoComposer.compose(taskBundle);
    if (taskInfos.isEmpty()) {
      return null;
    }
    Bundle updateBundle = new Bundle();
    updateBundle.setType(BundleType.TRANSACTION);

    TaskInfo taskInfo = taskInfos.get(0);
    Task task = taskInfo.getTask();
    ServiceRequest serviceRequest = taskInfo.getServiceRequestInfo()
        .getServiceRequest();
    if (StringUtils.hasText(update.getComment())) {
      UserDto user = contextService.getUser();
      task.addNote()
          .setText(update.getComment())
          .setTimeElement(DateTimeType.now())
          .setAuthor(new Reference(new IdType(user.getUserType(), user.getId())).setDisplay(user.getName()));
    }
    if (update.getStatus() != null) {
      if (Objects.equals(update.getStatus(), task.getStatus())) {
        throw new IllegalStateException("Invalid task status");
      }
      task.setStatus(update.getStatus())
          .setLastModifiedElement(DateTimeType.now());
      //TODO: remove after add validation to dto
      if (StringUtils.hasText(update.getStatusReason())) {
        task.setStatusReason(new CodeableConcept().setText(update.getStatusReason()));
      }
      updateBundle.addEntry(FhirUtil.createPutEntry(serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.REVOKED)));

      OrganizationInfo organizationInfo = organizationService.getOrganizationInfo(task.getOwner()
          .getReferenceElement()
          .getIdPart());
      try {
        Endpoint endpoint = organizationInfo.getEndpoint();
        TaskInfo cpTaskInfo = cpService.getTask(task.getIdElement()
            .getIdPart(), endpoint);
        Task cpTask = cpTaskInfo.getTask();
        cpTask.setStatus(task.getStatus());
        cpTask.setStatusReason(task.getStatusReason());
        cpTask.setLastModifiedElement(task.getLastModifiedElement());
        cpTask.setNote(task.getNote());
        ServiceRequest cpServiceRequest = cpTaskInfo.getServiceRequestInfo()
            .getServiceRequest();
        cpServiceRequest.setStatus(ServiceRequestStatus.REVOKED);

        Bundle cpUpdateBundle = new Bundle();
        cpUpdateBundle.setType(BundleType.TRANSACTION);
        cpUpdateBundle.addEntry(FhirUtil.createPutEntry(cpTask));
        cpUpdateBundle.addEntry(FhirUtil.createPutEntry(cpServiceRequest));

        cpService.transaction(cpUpdateBundle, endpoint);
      } catch (CpClientException e) {
        log.warn("Setting status FAILED for Task '" + task.getIdElement()
            .getIdPart() + "'. Reason: " + e.getMessage());
        task.setStatus(TaskStatus.FAILED)
            .setLastModifiedElement(DateTimeType.now())
            .setStatusReason(new CodeableConcept().setText(e.getMessage()));
      }
    }
    updateBundle.addEntry(FhirUtil.createPutEntry(task));
    taskRepository.transaction(updateBundle);
    return new TaskInfoToDtoConverter().convert(taskInfo);
  }

  protected void handleCpTask(Task task, Endpoint endpoint) {
    try {
      // Create task in CP.
      cpTaskCreateService.createTask(task, endpoint);
    } catch (CpTaskCreateException exc) {
      Bundle updateBundle = new Bundle();
      updateBundle.setType(Bundle.BundleType.TRANSACTION);
      log.warn(String.format("Task '%s' creation failed at CP. Failing a local Task and related ServiceRequest.",
          task.getIdElement()
              .getId()), exc);
      // If Task creation failed in CP - set local Task.status to FAILED and ServiceRequest status to REVOKED.
      // Additionally update a ServiceRequest status, but first - Read it.
      ServiceRequest serviceRequest = ehrClient.read()
          .resource(ServiceRequest.class)
          .withId(task.getFocus()
              .getReferenceElement())
          .execute();
      updateBundle.addEntry(FhirUtil.createPutEntry(task.setStatus(Task.TaskStatus.FAILED)
          .setLastModifiedElement(DateTimeType.now())
          .setStatusReason(new CodeableConcept().setText(exc.getMessage()))));
      updateBundle.addEntry(FhirUtil.createPutEntry(serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.REVOKED)));
      ehrClient.transaction()
          .withBundle(updateBundle)
          .execute();
    }
  }
}