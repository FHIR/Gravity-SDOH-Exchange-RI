package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.SearchModifierCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.TaskBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskService {

  private final IGenericClient cbroClient;

  public List<TaskDto> listTasks() {
    Bundle tasksBundle = cbroClient.search()
        .forResource(Task.class)
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.REQUESTED.toCode()))
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        // include ServiceRequest
        .include(Task.INCLUDE_FOCUS)
        .returnBundle(Bundle.class)
        .execute();
    return new TaskBundleToDtoConverter().convert(tasksBundle);
  }

  public TaskDto readTask(String taskId) {
    Bundle taskBundle = cbroClient.search()
        .forResource(Task.class)
        .where(Task.RES_ID.exactly()
            .code(taskId))
        // include ServiceRequest
        .include(Task.INCLUDE_FOCUS)
        .returnBundle(Bundle.class)
        .execute();
    return new TaskBundleToDtoConverter().convert(taskBundle)
        .stream()
        .findFirst()
        .orElse(null);
  }
}
