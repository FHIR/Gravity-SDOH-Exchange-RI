package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.CpTaskInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskBundleToDtoConverter implements Converter<Bundle, List<TaskDto>> {

  private final CpTaskInfoBundleExtractor cpTaskInfoBundleExtractor = new CpTaskInfoBundleExtractor();
  private final TaskToDtoConverter taskToDtoConverter = new TaskToDtoConverter();
  private final ServiceRequestToDtoConverter serviceRequestToDtoConverter = new ServiceRequestToDtoConverter();
  private final TypeToDtoConverter typeToDtoConverter = new TypeToDtoConverter();

  @Override
  public List<TaskDto> convert(Bundle bundle) {
    return cpTaskInfoBundleExtractor.extract(bundle)
        .stream()
        .map(cpTaskInfoHolder -> {
          TaskDto taskDto = taskToDtoConverter.convert(cpTaskInfoHolder.getTask());
          Assert.notNull(taskDto, "Task DTO cant be null.");
          taskDto.setServiceRequest(serviceRequestToDtoConverter.convert(cpTaskInfoHolder.getServiceRequest()));
          if (!Objects.isNull(cpTaskInfoHolder.getPerformer())) {
            taskDto.setPerformer(typeToDtoConverter.convert(FhirUtil.toReference(Organization.class,
                cpTaskInfoHolder.getPerformer()
                    .getId(), cpTaskInfoHolder.getPerformer()
                    .getName())));
          }
          return taskDto;
        })
        .collect(Collectors.toList());
  }
}