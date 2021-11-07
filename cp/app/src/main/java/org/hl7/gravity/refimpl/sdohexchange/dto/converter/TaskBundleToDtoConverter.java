package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class TaskBundleToDtoConverter implements Converter<Bundle, List<TaskDto>> {

  private final TaskInfoBundleExtractor taskInfoBundleParser = new TaskInfoBundleExtractor();
  private final TaskToDtoConverter taskToDtoConverter = new TaskToDtoConverter();
  private final ServiceRequestToDtoConverter serviceRequestToDtoConverter = new ServiceRequestToDtoConverter();

  @Override
  public List<TaskDto> convert(Bundle bundle) {
    FhirUtil.getFromBundle(bundle, Task.class)
        .stream()
        // Get filler-order tasks
        .filter(Task::hasBasedOn)
        .forEach(task -> ((Task) task.getBasedOn()
            .get(0)
            .getResource()).setOwnerTarget((Resource) task.getOwner()
            .getResource()));
    // Return Tasks where search.mode != include and all ServiceRequests
    bundle.setEntry(bundle.getEntry()
        .stream()
        .filter(entry -> entry.getResource()
            .getResourceType()
            .equals(ResourceType.ServiceRequest) || (entry.getResource()
            .getResourceType()
            .equals(ResourceType.Task) && !entry.getSearch()
            .getMode()
            .equals(Bundle.SearchEntryMode.INCLUDE)))
        .collect(Collectors.toList()));
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