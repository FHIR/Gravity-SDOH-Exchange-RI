package org.hl7.gravity.refimpl.sdohexchange.dto.converter.info;

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
    Task task = taskInfo.getTask();
    TaskDto taskDto = new TaskDto(task.getIdElement()
        .getIdPart());
    //Convert Task
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
    taskDto.setOutcome(task.getStatusReason()
        .getText());
    // TODO validate profile and other properties using InstanceValidator
    //Convert ServiceRequest
    String serviceRequestId = new IdType(task.getFocus()
        .getReference()).toUnqualifiedVersionless()
        .getIdPart();
    ServiceRequestInfo serviceRequestInfo = taskInfo.getServiceRequests()
        .get(serviceRequestId);
    if (serviceRequestInfo == null) {
      taskDto.getErrors()
          .add("Task.focus not set or is not a ServiceRequest.");
    } else {
      taskDto.setServiceRequest(serviceRequestInfoToDtoConverter.convert(serviceRequestInfo));
    }
    //Convert Organization
    Organization organization = taskInfo.getOrganizations()
        .get(new IdType(taskInfo.getTask()
            .getOwner()
            .getReference()).toUnqualifiedVersionless()
            .getIdPart());
    if (organization == null) {
      taskDto.getErrors()
          .add("Task.owner not set or is not an Organization.");
    } else {
      taskDto.setOrganization(organizationToDtoConverter.convert(organization));
    }
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
