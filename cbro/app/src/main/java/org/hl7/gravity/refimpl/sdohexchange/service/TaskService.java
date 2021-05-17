package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.context.FhirContext;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.TaskRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.TaskBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.UpdateTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskUpdateBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.info.TaskInfo;
import org.hl7.gravity.refimpl.sdohexchange.info.composer.TasksInfoComposer;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskService {

  private final TasksInfoComposer tasksInfoComposer;
  private final TaskRepository taskRepository;
  private final SDOHMappings sdohMappings;
  private final FhirContext fhirContext;

  public List<TaskDto> readAll() {
    return new TaskBundleToDtoConverter().convert(
        taskRepository.findAll(Collections.singletonList(Task.TaskStatus.REQUESTED)));
  }

  public TaskDto read(String id) {
    Bundle taskBundle = taskRepository.find(id, Collections.singleton(Task.INCLUDE_FOCUS));
    List<TaskInfo> taskInfos = tasksInfoComposer.compose(taskBundle);
    if (taskInfos.isEmpty()) {
      return null;
    }
    return new TaskBundleToDtoConverter().convert(taskBundle)
        .stream()
        .findFirst()
        .orElse(null);
  }

  public TaskDto update(String id, UpdateTaskRequestDto update, UserDto user) {
    // Validates and converts Procedure codes to Coding
    List<Coding> procedureCodes = update.getProcedureCodes()
        .stream()
        .map(code -> sdohMappings.findCoding(Procedure.class, code))
        .collect(Collectors.toList());

    Bundle taskBundle = taskRepository.find(id, Collections.singleton(Task.INCLUDE_FOCUS));
    List<TaskInfo> taskInfos = tasksInfoComposer.compose(taskBundle);
    if (taskInfos.isEmpty()) {
      return null;
    }
    TaskInfo taskInfo = taskInfos.get(0);
    Task task = taskInfo.getTask();
    ServiceRequest serviceRequest = taskInfo.getServiceRequestInfo()
        .getServiceRequest();

    TaskUpdateBundleFactory bundleFactory =
        new TaskUpdateBundleFactory(task, update.getStatus(), update.getStatusReason(), update.getComment(),
            update.getOutcome(), procedureCodes);
    bundleFactory.setUser(user);
    bundleFactory.setServiceRequest(serviceRequest);
    bundleFactory.setConditions(FhirUtil.getReferences(fhirContext, serviceRequest, Condition.class));

    Bundle updateBundle = bundleFactory.createBundle();

    taskRepository.transaction(updateBundle);
    return new TaskBundleToDtoConverter().convert(taskBundle)
        .stream()
        .findFirst()
        .orElse(null);
  }
}