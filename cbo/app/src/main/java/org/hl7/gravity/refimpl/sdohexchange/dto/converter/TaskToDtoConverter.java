package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ProcedureDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.springframework.core.convert.converter.Converter;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil.toLocalDateTime;

// Copied from cp/app/src/main/java/org/hl7/gravity/refimpl/sdohexchange/dto/converter/
public class TaskToDtoConverter implements Converter<Task, TaskDto> {

  private final TypeToDtoConverter typeToDtoConverter = new TypeToDtoConverter();
  private final AnnotationToDtoConverter annotationToDtoConverter = new AnnotationToDtoConverter();

  @Override
  public TaskDto convert(Task task) {
    TaskDto taskDto = new TaskDto();
    taskDto.setId(task.getIdElement()
        .getIdPart());
    taskDto.setName(task.getDescription());
    taskDto.setCreatedAt(toLocalDateTime(task.getAuthoredOnElement()));
    taskDto.setLastModified(toLocalDateTime(task.getLastModifiedElement()));
    taskDto.setPriority(Optional.ofNullable(task.getPriority())
        .map(Task.TaskPriority::getDisplay)
        .orElse(null));
    taskDto.setStatus(task.getStatus()
        .getDisplay());
    taskDto.setRequester(typeToDtoConverter.convert(task.getRequester()));
    taskDto.setPatient(typeToDtoConverter.convert(task.getFor()));
    //TODO: Change to consent id in future
    taskDto.setConsent("yes");
    taskDto.setComments(task.getNote()
        .stream()
        .map(annotationToDtoConverter::convert)
        .collect(Collectors.toList()));
    taskDto.setStatusReason(task.getStatusReason()
        .getText());

    for (Task.TaskOutputComponent outputComponent : task.getOutput()) {
      Type componentValue = outputComponent.getValue();
      if (componentValue instanceof Reference) {
        Reference procedureReference = (Reference) componentValue;
        taskDto.getProcedures()
            .add(new ProcedureDto(procedureReference.getReferenceElement()
                .getIdPart(), procedureReference.getDisplay()));
      } else if (componentValue instanceof CodeableConcept) {
        CodeableConcept outcome = (CodeableConcept) componentValue;
        taskDto.setOutcome(outcome.getText());
      }
    }
    return taskDto;
  }
}
