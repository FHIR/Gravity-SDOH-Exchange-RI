package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask.PatientTaskDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskItemInfoBundleExtractor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PatientTaskBundleToDtoConverter implements Converter<Bundle, List<PatientTaskDto>> {

  private final PatientTaskInfoHolderToDtoConverter taskToDtoConverter = new PatientTaskInfoHolderToDtoConverter();
  PatientTaskItemInfoBundleExtractor extractor = new PatientTaskItemInfoBundleExtractor();

  @Override
  public List<PatientTaskDto> convert(Bundle bundle) {
    return extractor.extract(bundle)
        .stream()
        .map(itemInfoHolder -> {
          PatientTaskDto taskDto = taskToDtoConverter.convert(itemInfoHolder);
          Assert.notNull(taskDto, "Task DTO cant be null.");
          return taskDto;
        })
        .collect(Collectors.toList());
  }
}