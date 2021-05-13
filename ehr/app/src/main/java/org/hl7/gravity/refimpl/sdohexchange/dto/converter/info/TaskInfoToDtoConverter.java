package org.hl7.gravity.refimpl.sdohexchange.dto.converter.info;

import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskOutputComponent;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.AnnotationToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.OrganizationToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.Priority;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ProcedureDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.info.ServiceRequestInfo;
import org.hl7.gravity.refimpl.sdohexchange.info.TaskInfo;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class TaskInfoToDtoConverter implements Converter<TaskInfo, TaskDto> {

  private final ServiceRequestInfoToDtoConverter serviceRequestInfoToDtoConverter;
  private final OrganizationToDtoConverter organizationToDtoConverter;
  private final AnnotationToDtoConverter annotationToDtoConverter;

  public TaskInfoToDtoConverter() {
    serviceRequestInfoToDtoConverter = new ServiceRequestInfoToDtoConverter();
    organizationToDtoConverter = new OrganizationToDtoConverter();
    annotationToDtoConverter = new AnnotationToDtoConverter();
  }

  @Override
  public TaskDto convert(TaskInfo taskInfo) {
    return composeTaskDto(taskInfo);
  }

  protected TaskDto composeTaskDto(TaskInfo taskInfo) {
    Task task = taskInfo.getTask();
    TaskDto taskDto = new TaskDto(task.getIdElement()
        .getIdPart());
    //Convert Task
    taskDto.setName(task.getDescription());
    taskDto.setPriority(Priority.fromText(task.getPriority()
        .getDisplay()));
    taskDto.setCreatedAt(FhirUtil.toLocalDateTime(task.getAuthoredOnElement()));
    taskDto.setLastModified(FhirUtil.toLocalDateTime(task.getLastModifiedElement()));
    Optional.ofNullable(task.getStatus())
        .ifPresent(s -> taskDto.setStatus(Task.TaskStatus.fromCode(s.toCode())
            .getDisplay()));
    taskDto.setComments(task.getNote()
        .stream()
        .map(annotationToDtoConverter::convert)
        .collect(Collectors.toList()));
    taskDto.setOutcome(task.getStatusReason()
        .getText());
    // TODO validate profile and other properties using InstanceValidator
    //Convert ServiceRequest
    String srId = new IdType(task.getFocus()
        .getReference()).toUnqualifiedVersionless()
        .getIdPart();
    ServiceRequestInfo srInfo = taskInfo.getServiceRequests()
        .get(srId);
    if (srInfo == null) {
      taskDto.getErrors()
          .add("Task.focus not set or is not a ServiceRequest.");
    } else {
      taskDto.setServiceRequest(serviceRequestInfoToDtoConverter.convert(srInfo));
    }
    //Convert Organization
    Organization org = taskInfo.getOrganizations()
        .get(new IdType(taskInfo.getTask()
            .getOwner()
            .getReference()).toUnqualifiedVersionless()
            .getIdPart());
    if (org == null) {
      taskDto.getErrors()
          .add("Task.owner not set or is not an Organization.");
    } else {
      taskDto.setOrganization(organizationToDtoConverter.convert(org));
    }
    for (TaskOutputComponent outputComponent : task.getOutput()) {
      Type componentValue = outputComponent.getValue();
      if (Reference.class.isInstance(componentValue)) {
        Reference procedureReference = (Reference) componentValue;
        taskDto.getProcedures()
            .add(new ProcedureDto(procedureReference.getReferenceElement()
                .getIdPart(), procedureReference.getDisplay()));
      } else if (CodeableConcept.class.isInstance(componentValue)) {
        CodeableConcept outcome = (CodeableConcept) componentValue;
        taskDto.setOutcome(outcome.getText());
      }
    }
    return taskDto;
  }
}
