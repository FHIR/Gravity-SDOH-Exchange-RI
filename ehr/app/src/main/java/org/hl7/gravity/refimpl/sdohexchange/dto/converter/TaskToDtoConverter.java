package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskOutputComponent;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ProcedureDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

public class TaskToDtoConverter implements Converter<Task, TaskDto> {

  private final TypeToDtoConverter typeToDtoConverter = new TypeToDtoConverter();
  private final AnnotationToDtoConverter annotationToDtoConverter = new AnnotationToDtoConverter();

  @Override
  public TaskDto convert(Task task) {
    TaskDto taskDto = new TaskDto();
    taskDto.setId(task.getIdElement()
        .getIdPart());
    taskDto.setName(task.getDescription());
    taskDto.setCreatedAt(FhirUtil.toLocalDateTime(task.getAuthoredOnElement()));
    taskDto.setLastModified(FhirUtil.toLocalDateTime(task.getLastModifiedElement()));
    taskDto.setPriority(task.getPriority()
        .getDisplay());
    taskDto.setStatus(task.getStatus()
        .getDisplay());
    taskDto.setComments(task.getNote()
        .stream()
        .map(annotationToDtoConverter::convert)
        .collect(Collectors.toList()));
    taskDto.setStatusReason(task.getStatusReason()
        .getText());
    taskDto.setOrganization(typeToDtoConverter.convert(task.getOwner()));
    for (TaskOutputComponent outputComponent : task.getOutput()) {
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
