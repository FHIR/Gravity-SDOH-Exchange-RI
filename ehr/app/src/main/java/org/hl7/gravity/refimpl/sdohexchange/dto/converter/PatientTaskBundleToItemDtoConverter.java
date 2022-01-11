package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask.PatientTaskItemDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskItemInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskItemInfoBundleExtractor.PatientTaskItemInfoHolder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class PatientTaskBundleToItemDtoConverter implements Converter<Bundle, List<PatientTaskItemDto>> {

  private final PatientTaskItemInfoHolderToItemDtoConverter<PatientTaskItemInfoHolder>
      taskToDtoConverter = new PatientTaskItemInfoHolderToItemDtoConverter();
  private final PatientTaskItemInfoBundleExtractor extractor = new PatientTaskItemInfoBundleExtractor();

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