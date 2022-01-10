package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OccurrenceResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask.PatientTaskDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskInfoBundleExtractor.PatientTaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.stream.Collectors;

public class PatientTaskInfoHolderToDtoConverter
    extends PatientTaskItemInfoHolderToItemDtoConverter<PatientTaskInfoHolder, PatientTaskDto> {

  private final AnnotationToDtoConverter annotationToDtoConverter = new AnnotationToDtoConverter();

  @Override
  public PatientTaskDto convert(PatientTaskInfoHolder taskInfoHolder) {
    Task task = taskInfoHolder.getTask();
    PatientTaskDto taskDto = super.convert(taskInfoHolder);
    taskDto.setCreatedAt(FhirUtil.toLocalDateTime(task.getAuthoredOnElement()));
    taskDto.setComments(task.getNote()
        .stream()
        .map(annotationToDtoConverter::convert)
        .collect(Collectors.toList()));
    taskDto.setOccurrence(new OccurrenceResponseDto(FhirUtil.toLocalDateTime(task.getExecutionPeriod()
        .getStartElement()), FhirUtil.toLocalDateTime(task.getExecutionPeriod()
        .getEndElement())));
    if (taskDto.getAssessment() == null && taskInfoHolder.getQuestionnaireResponse() != null) {
      taskDto.setAnswers(taskInfoHolder.getQuestionnaireResponse()
          .getItem()
          .stream()
          .collect(Collectors.toMap(qr -> qr.getText(), qr -> ((Coding) qr.getAnswerFirstRep()
              .getValue()).getDisplay())));
    }
    return taskDto;
  }

  protected PatientTaskDto createDto() {
    return new PatientTaskDto();
  }
}
