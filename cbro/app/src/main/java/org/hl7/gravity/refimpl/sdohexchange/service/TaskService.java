package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
//TODO: To be implemented
public class TaskService {

  private final IGenericClient cbroClient;

  public List<TaskDto> listTasks() {
    return null;
  }

  public TaskDto readTask(String taskId) {
    return null;
  }
}
