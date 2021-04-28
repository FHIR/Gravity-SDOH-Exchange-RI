package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.Priority;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class TaskBundleToDtoConverter implements Converter<Bundle, List<TaskDto>> {

  private final ServiceRequestToDtoConverter serviceRequestToDtoConverter = new ServiceRequestToDtoConverter();
  private final OrganizationToDtoConverter organizationToDtoConverter = new OrganizationToDtoConverter();
  private final AnnotationToDtoConverter annotationToDtoConverter = new AnnotationToDtoConverter();

  @Override
  public List<TaskDto> convert(Bundle bundle) {
    // Retrieve all Task.focus ServiceRequest instances
    Map<String, ServiceRequest> srMap = FhirUtil.getFromBundle(bundle, ServiceRequest.class)
        .stream()
        .collect(Collectors.toMap(r -> r.getIdElement()
            .getIdPart(), Function.identity()));

    // Retrieve all Task.owner Organization instances
    Map<String, Organization> orgMap = FhirUtil.getFromBundle(bundle, Organization.class)
        .stream()
        .collect(Collectors.toMap(r -> r.getIdElement()
            .getIdPart(), Function.identity()));

    return FhirUtil.getFromBundle(bundle, Task.class)
        .stream()
        .map(t -> composeTaskDto(t, srMap, orgMap))
        .collect(Collectors.toList());
  }

  protected TaskDto composeTaskDto(Task tasl, Map<String, ServiceRequest> srMap, Map<String, Organization> orgMap) {
    TaskDto taskDto = new TaskDto(tasl.getIdElement()
        .getIdPart());
    //Convert Task
    taskDto.setName(tasl.getDescription());
    taskDto.setPriority(Priority.fromText(tasl.getPriority()
        .getDisplay()));
    taskDto.setCreatedAt(FhirUtil.toLocalDateTime(tasl.getAuthoredOnElement()));
    taskDto.setLastModified(FhirUtil.toLocalDateTime(tasl.getLastModifiedElement()));
    Optional.ofNullable(tasl.getStatus())
        .ifPresent(s -> taskDto.setStatus(Task.TaskStatus.fromCode(s.toCode())));
    taskDto.setComments(tasl.getNote()
        .stream()
        .map(annotationToDtoConverter::convert)
        .collect(Collectors.toList()));
    taskDto.setOutcome(tasl.getStatusReason()
        .getText());
    // TODO validate profile and other properties using InstanceValidator
    //Convert ServiceRequest
    String srId = new IdType(tasl.getFocus()
        .getReference()).toUnqualifiedVersionless()
        .getIdPart();
    ServiceRequest sr = srMap.get(srId);
    if (sr == null) {
      taskDto.getErrors()
          .add("Task.focus not set or is not a ServiceRequest.");
    } else {
      taskDto.setServiceRequest(serviceRequestToDtoConverter.convert(sr));
    }
    //Convert Organization
    Organization org = orgMap.get(new IdType(tasl.getOwner()
        .getReference()).toUnqualifiedVersionless()
        .getIdPart());
    if (org == null) {
      taskDto.getErrors()
          .add("Task.owner not set or is not an Organization.");
    } else {
      taskDto.setOrganization(organizationToDtoConverter.convert(org));
    }
    return taskDto;
  }
}
