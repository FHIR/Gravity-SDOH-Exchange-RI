package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask.PatientTaskDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskInfoBundleExtractor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class PatientTaskBundleToDtoConverter implements Converter<Bundle, List<PatientTaskDto>> {

  private final PatientTaskInfoHolderToDtoConverter taskToDtoConverter = new PatientTaskInfoHolderToDtoConverter();
  private final PatientTaskInfoBundleExtractor extractor = new PatientTaskInfoBundleExtractor();

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