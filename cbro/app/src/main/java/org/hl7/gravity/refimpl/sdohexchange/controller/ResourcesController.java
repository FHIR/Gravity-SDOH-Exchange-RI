package org.hl7.gravity.refimpl.sdohexchange.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.TaskRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskResourcesDto;
import org.hl7.gravity.refimpl.sdohexchange.info.ServiceRequestInfo;
import org.hl7.gravity.refimpl.sdohexchange.info.TaskInfo;
import org.hl7.gravity.refimpl.sdohexchange.info.composer.TasksInfoComposer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {SpringFoxConfig.RESOURCES_API_TAG})
@Validated
@RequestMapping("/resources")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
public class ResourcesController {

  private final FhirContext fhirContext;
  private final TaskRepository taskRepository;
  private final TasksInfoComposer tasksInfoComposer;

  @ApiOperation(value = "Get all Task resources.")
  @GetMapping("/task/{id}")
  public ResponseEntity<TaskResourcesDto> takResources(@PathVariable @NotBlank String id) {
    Bundle taskBundle =
        taskRepository.find(id, Arrays.asList(Task.INCLUDE_FOCUS, Task.INCLUDE_SUBJECT, Task.INCLUDE_REQUESTER));
    List<TaskInfo> taskInfos = tasksInfoComposer.compose(taskBundle);
    if (taskInfos.isEmpty()) {
      return ResponseEntity.notFound()
          .build();
    }
    TaskInfo taskInfo = taskInfos.get(0);
    ServiceRequestInfo serviceRequestInfo = taskInfo.getServiceRequestInfo();
    IParser jsonParser = fhirContext.newJsonParser();

    TaskResourcesDto resourcesDto = new TaskResourcesDto();
    resourcesDto.setTask(jsonParser.encodeResourceToString(taskInfo.getTask()));
    resourcesDto.setServiceRequest(jsonParser.encodeResourceToString(serviceRequestInfo
        .getServiceRequest()));
    resourcesDto.setPatient(jsonParser.encodeResourceToString(taskInfo.getPatient()));
    resourcesDto.setRequester(jsonParser.encodeResourceToString(taskInfo.getRequester()));
    resourcesDto.setConsent(jsonParser.encodeResourceToString(serviceRequestInfo.getConsent()));
    resourcesDto.setGoals(serviceRequestInfo.getGoals()
        .stream()
        .map(jsonParser::encodeResourceToString)
        .collect(Collectors.toList()));
    resourcesDto.setConditions(serviceRequestInfo.getConditions()
        .stream()
        .map(jsonParser::encodeResourceToString)
        .collect(Collectors.toList()));
    return ResponseEntity.ok(resourcesDto);
  }
}