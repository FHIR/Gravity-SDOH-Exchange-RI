package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

// Copied from cp/app/src/main/java/org/hl7/gravity/refimpl/sdohexchange/dto/converter/
// Added serverId field, constructor for it and setting this field to TaskDto
public class TaskBundleToDtoConverter implements Converter<Bundle, List<TaskDto>> {

  private final TaskInfoBundleExtractor taskInfoBundleParser = new TaskInfoBundleExtractor();
  private final TaskToDtoConverter taskToDtoConverter = new TaskToDtoConverter();
  private final ServiceRequestToDtoConverter serviceRequestToDtoConverter = new ServiceRequestToDtoConverter();
  private final Integer serverId;

  public TaskBundleToDtoConverter(Integer serverId) {
    this.serverId = serverId;
  }

  @Override
  public List<TaskDto> convert(Bundle bundle) {
    return taskInfoBundleParser.extract(bundle)
        .stream()
        .map(taskInfoHolder -> {
          TaskDto taskDto = taskToDtoConverter.convert(taskInfoHolder.getTask());
          Assert.notNull(taskDto, "Task DTO cant be null.");
          taskDto.setServerId(serverId);
          taskDto.setServiceRequest(serviceRequestToDtoConverter.convert(taskInfoHolder.getServiceRequest()));
          return taskDto;
        })
        .collect(Collectors.toList());
  }
}
