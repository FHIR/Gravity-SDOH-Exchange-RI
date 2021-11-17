package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.TaskRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.OurTaskBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.TaskReadException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskUpdateBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OurTaskService {

  private final TaskRepository taskRepository;

  public List<TaskDto> readAll() {
    Bundle tasksBundle = taskRepository.findAllOurTasks();
    return new OurTaskBundleToDtoConverter().convert(tasksBundle);
  }

  public TaskDto read(String id) {
    Bundle taskBundle = taskRepository.find(id,
        Lists.newArrayList(Task.INCLUDE_FOCUS, Task.INCLUDE_BASED_ON, Task.INCLUDE_OWNER.asNonRecursive()));
    Task ourTask = FhirUtil.getFirstFromBundle(taskBundle, Task.class);
    if (Objects.isNull(ourTask)) {
      throw new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id));
    }
    if (!ourTask.getIntent()
        .equals(Task.TaskIntent.FILLERORDER)) {
      throw new TaskReadException("The intent of Task/" + id + " is not filler-order.");
    }
    return new OurTaskBundleToDtoConverter().convert(taskBundle)
        .stream()
        .findFirst()
        .get();
  }

  public void update(String id, UpdateOurTaskRequestDto update) {
    Bundle taskBundle = taskRepository.find(id, Lists.newArrayList(Task.INCLUDE_FOCUS));
    TaskInfoBundleExtractor.TaskInfoHolder taskInfo = new TaskInfoBundleExtractor().extract(taskBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id)));
    TaskUpdateBundleFactory bundleFactory = new TaskUpdateBundleFactory();
    bundleFactory.setTask(taskInfo.getTask());
    bundleFactory.setServiceRequest(taskInfo.getServiceRequest());
    bundleFactory.setStatus(update.getTaskStatus());
    bundleFactory.setStatusReason(update.getStatusReason());
    bundleFactory.setComment(update.getComment());
    taskRepository.transaction(bundleFactory.createUpdateBundle());
  }
}