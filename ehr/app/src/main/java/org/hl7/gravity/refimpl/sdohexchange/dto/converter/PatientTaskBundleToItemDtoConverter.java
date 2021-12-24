package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask.PatientTaskItemDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskItemInfoBundleExtractor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PatientTaskBundleToItemDtoConverter implements Converter<Bundle, List<PatientTaskItemDto>> {

  private final PatientTaskInfoHolderToItemDtoConverter taskToDtoConverter =
      new PatientTaskInfoHolderToItemDtoConverter();
  PatientTaskItemInfoBundleExtractor extractor = new PatientTaskItemInfoBundleExtractor();

  @Override
  public List<PatientTaskItemDto> convert(Bundle bundle) {
    return extractor.extract(bundle)
        .stream()
        .map(itemInfoHolder -> {
          PatientTaskItemDto taskDto = taskToDtoConverter.convert(itemInfoHolder);
          Assert.notNull(taskDto, "Task DTO cant be null.");
          return taskDto;
        })
        .collect(Collectors.toList());
  }
}