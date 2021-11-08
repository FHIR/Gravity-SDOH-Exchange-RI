package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.TaskRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.TaskBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OurTaskService {

  private final TaskRepository taskRepository;

  public List<TaskDto> readAll() {
    Bundle tasksBundle = taskRepository.findAllOurTasks();
    return new TaskBundleToDtoConverter().convert(tasksBundle);
  }

  public TaskDto read(String id) {
    Bundle taskBundle = taskRepository.find(id, Lists.newArrayList(Task.INCLUDE_FOCUS));
    return new TaskBundleToDtoConverter().convert(taskBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id)));
  }
}