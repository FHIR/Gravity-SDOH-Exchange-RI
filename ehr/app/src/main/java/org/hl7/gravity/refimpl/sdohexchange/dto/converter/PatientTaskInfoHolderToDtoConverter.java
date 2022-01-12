package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OccurrenceResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask.PatientTaskDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask.PatientTaskInfoBundleExtractor.PatientTaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class PatientTaskInfoHolderToDtoConverter
    extends PatientTaskItemInfoHolderToItemDtoConverter<PatientTaskInfoHolder> {

  private final AnnotationToDtoConverter annotationToDtoConverter = new AnnotationToDtoConverter();

  @Override
  public PatientTaskDto convert(PatientTaskInfoHolder taskInfoHolder) {
    Task task = taskInfoHolder.getTask();
    PatientTaskDto taskDto = (PatientTaskDto) super.convert(taskInfoHolder);
    taskDto.setCreatedAt(FhirUtil.toLocalDateTime(task.getAuthoredOnElement()));
    taskDto.setComments(task.getNote()
        .stream()
        .map(annotationToDtoConverter::convert)
        .collect(Collectors.toList()));
    taskDto.setOccurrence(new OccurrenceResponseDto(FhirUtil.toLocalDateTime(task.getExecutionPeriod()
        .getStartElement()), FhirUtil.toLocalDateTime(task.getExecutionPeriod()
        .getEndElement())));
    if (taskInfoHolder.getQuestionnaireResponse() != null) {
      taskDto.setAnswers(taskInfoHolder.getQuestionnaireResponse()
          .getItem()
          .stream()
          .collect(Collectors.toMap(qr -> qr.getText(), qr -> {
            Type itemAnswer = qr.getAnswerFirstRep()
                .getValue();
            if (itemAnswer instanceof StringType) {
              return ((StringType) itemAnswer).getValue();
            } else if (itemAnswer instanceof Coding) {
              return ((Coding) itemAnswer).getDisplay();
            } else {
              taskDto.getErrors()
                  .add(String.format("Answer cannot be resolved. %s type is not expected.", itemAnswer.getClass()
                      .getSimpleName()));
              return "Answer cannot be parsed.";
            }
          }, (existing, replacement) -> {
            taskDto.getErrors()
                .add(String.format("Duplicating questions detected. Value '%s' overwrites the value '%s'", replacement,
                    existing));
            return replacement;
          }, LinkedHashMap::new)));
    }
    return taskDto;
  }

  protected PatientTaskDto createDto() {
    return new PatientTaskDto();
  }
}
