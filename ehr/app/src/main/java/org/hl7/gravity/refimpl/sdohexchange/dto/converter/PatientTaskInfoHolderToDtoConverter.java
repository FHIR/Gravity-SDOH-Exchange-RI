package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.PatientTaskInfoBundleExtractor.PatientTaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask.PatientTaskDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.stream.Collectors;

public class PatientTaskInfoHolderToDtoConverter extends PatientTaskInfoHolderToItemDtoConverter {

  private final AnnotationToDtoConverter annotationToDtoConverter = new AnnotationToDtoConverter();

  public PatientTaskDto convert(PatientTaskInfoHolder taskInfoHolder) {
    Task task = taskInfoHolder.getTask();
    PatientTaskDto taskDto = (PatientTaskDto) super.convert(taskInfoHolder);
    taskDto.setCreatedAt(FhirUtil.toLocalDateTime(task.getAuthoredOnElement()));
    taskDto.setComments(task.getNote()
        .stream()
        .map(annotationToDtoConverter::convert)
        .collect(Collectors.toList()));
    if (taskInfoHolder.getQuestionnaireResponse() != null) {
      taskDto.setAnswers(taskInfoHolder.getQuestionnaireResponse()
          .getItem()
          .stream()
          .collect(Collectors.toMap(qr -> qr.getText(), qr -> ((Coding) qr.getAnswerFirstRep()
              .getValue()).getDisplay())));
    }
    return taskDto;
  }

  @Override
  protected PatientTaskDto createDto() {
    return new PatientTaskDto();
  }
}
