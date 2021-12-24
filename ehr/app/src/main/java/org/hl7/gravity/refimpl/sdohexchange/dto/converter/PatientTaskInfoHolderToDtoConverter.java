package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask.PatientTaskDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskItemInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.stream.Collectors;

public class PatientTaskInfoHolderToDtoConverter extends PatientTaskInfoHolderToItemDtoConverter {

  private final AnnotationToDtoConverter annotationToDtoConverter = new AnnotationToDtoConverter();

  @Override
  public PatientTaskDto convert(PatientTaskItemInfoBundleExtractor.PatientTaskItemInfoHolder taskInfoHolder) {
    Task task = taskInfoHolder.getTask();
    PatientTaskDto taskDto = (PatientTaskDto) super.convert(taskInfoHolder);
    taskDto.setCreatedAt(FhirUtil.toLocalDateTime(task.getAuthoredOnElement()));
    taskDto.setComments(task.getNote()
        .stream()
        .map(annotationToDtoConverter::convert)
        .collect(Collectors.toList()));
    return taskDto;
  }

  @Override
  protected PatientTaskDto createDto() {
    return new PatientTaskDto();
  }
}
