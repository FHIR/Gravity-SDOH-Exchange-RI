package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.OurTaskInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class OurTaskBundleToDtoConverter implements Converter<Bundle, List<TaskDto>> {

  private final OurTaskInfoBundleExtractor ourTaskInfoBundleExtractor = new OurTaskInfoBundleExtractor();
  private final TaskToDtoConverter taskToDtoConverter = new TaskToDtoConverter();
  private final ServiceRequestToDtoConverter serviceRequestToDtoConverter = new ServiceRequestToDtoConverter();
  private final TypeToDtoConverter typeToDtoConverter = new TypeToDtoConverter();

  @Override
  public List<TaskDto> convert(Bundle bundle) {
    return ourTaskInfoBundleExtractor.extract(bundle)
        .stream()
        .map(ourTaskInfoHolder -> {
          TaskDto taskDto = taskToDtoConverter.convert(ourTaskInfoHolder.getTask());
          Assert.notNull(taskDto, "Task DTO cant be null.");
          taskDto.setServiceRequest(serviceRequestToDtoConverter.convert(ourTaskInfoHolder.getServiceRequest()));
          taskDto.setBaseTask(typeToDtoConverter.convert(FhirUtil.toReference(Task.class,
              ourTaskInfoHolder.getBaseTask()
                  .getIdElement()
                  .getIdPart(), ourTaskInfoHolder.getBaseTask()
                  .getDescription())));
          taskDto.setPerformer(typeToDtoConverter.convert(FhirUtil.toReference(Organization.class,
              ourTaskInfoHolder.getPerformer()
                  .getId(), ourTaskInfoHolder.getPerformer()
                  .getName())));
          return taskDto;
        })
        .collect(Collectors.toList());
  }
}
