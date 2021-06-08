package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

@Slf4j
public class TaskBundleToDtoConverter implements Converter<Bundle, List<TaskDto>> {

  private final TaskInfoBundleExtractor taskInfoBundleParser = new TaskInfoBundleExtractor();
  private final TaskToDtoConverter taskToDtoConverter = new TaskToDtoConverter();
  private final ServiceRequestToDtoConverter serviceRequestToDtoConverter = new ServiceRequestToDtoConverter();

  @Override
  public List<TaskDto> convert(Bundle bundle) {
    return taskInfoBundleParser.extract(bundle)
        .stream()
        .map(taskInfoHolder -> {
          TaskDto taskDto = taskToDtoConverter.convert(taskInfoHolder.getTask());
          Assert.notNull(taskDto, "Task DTO cant be null.");
          taskDto.setServiceRequest(serviceRequestToDtoConverter.convert(taskInfoHolder.getServiceRequest()));
          return taskDto;
        })
        .collect(Collectors.toList());
  }
}