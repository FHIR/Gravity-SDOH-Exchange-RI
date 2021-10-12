package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.TaskRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.TaskBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.UpdateTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor.TaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskUpdateBundleFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A Service to work with CP Tasks (usually retrieved from the EHR). It skips any Tasks created by this CP for the
 * different CBOs.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskService {

  private final TaskRepository taskRepository;
  private final SDOHMappings sdohMappings;

  public List<TaskDto> readAll() {
    Bundle tasksBundle = taskRepository.findAllTasks();
    return new TaskBundleToDtoConverter().convert(tasksBundle);
  }

  public TaskDto read(String id) {
    Bundle taskBundle = taskRepository.find(id, Lists.newArrayList(Task.INCLUDE_FOCUS));
    return new TaskBundleToDtoConverter().convert(taskBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id)));
  }

  public void update(String id, UpdateTaskRequestDto update, UserDto user) {
    // Validates and converts Procedure codes to Coding
    List<Coding> procedureCodes = Optional.ofNullable(update.getProcedureCodes())
        .orElse(Collections.emptyList())
        .stream()
        .map(code -> sdohMappings.findResourceCoding(Procedure.class, code))
        .collect(Collectors.toList());
    Bundle taskBundle = taskRepository.find(id, Lists.newArrayList(Task.INCLUDE_FOCUS));
    TaskInfoHolder taskInfo = new TaskInfoBundleExtractor().extract(taskBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id)));
    TaskUpdateBundleFactory bundleFactory = new TaskUpdateBundleFactory();
    bundleFactory.setTask(taskInfo.getTask());
    bundleFactory.setServiceRequest(taskInfo.getServiceRequest());
    bundleFactory.setStatus(update.getTaskStatus());
    bundleFactory.setStatusReason(update.getStatusReason());
    bundleFactory.setComment(update.getComment());
    bundleFactory.setOutcome(update.getOutcome());
    bundleFactory.setProcedureCodes(procedureCodes);
    bundleFactory.setUser(user);
    taskRepository.transaction(bundleFactory.createUpdateBundle());
  }
}