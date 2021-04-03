package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.TaskCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class TaskBundleToDtoConverter implements Converter<Bundle, List<TaskDto>> {

  private final ServiceRequestToDtoConverter serviceRequestToDtoConverter = new ServiceRequestToDtoConverter();
  private final OrganizationToDtoConverter organizationToDtoConverter = new OrganizationToDtoConverter();

  @Override
  public List<TaskDto> convert(Bundle bundle) {

    // Retrieve all Task.focus ServiceRequest instances
    Map<String, ServiceRequest> srMap = FhirUtil.getFromBundle(bundle, ServiceRequest.class)
        .stream()
        .collect(Collectors.toMap(r -> r.getIdElement()
            .getIdPart(), r -> r));

    // Retrieve all Task.owner Organization instances
    Map<String, Organization> orgMap = FhirUtil.getFromBundle(bundle, Organization.class)
        .stream()
        .collect(Collectors.toMap(r -> r.getIdElement()
            .getIdPart(), r -> r));

    return FhirUtil.getFromBundle(bundle, Task.class)
        .stream()
        .map(t -> composeTaskDto(t, srMap, orgMap))
        .collect(Collectors.toList());
  }

  protected TaskDto composeTaskDto(Task t, Map<String, ServiceRequest> srMap, Map<String, Organization> orgMap) {
    TaskDto taskDto = new TaskDto(t.getIdElement()
        .getIdPart());
    //Convert Task
    taskDto.setType(TaskCode.fromCode(t.getCode()
        .getCodingFirstRep()
        .getCode()));
    taskDto.setCreatedAt(FhirUtil.toLocalDateTime(t.getAuthoredOnElement()));
    taskDto.setLastModified(FhirUtil.toLocalDateTime(t.getLastModifiedElement()));
    Optional.ofNullable(t.getStatus())
        .ifPresent(s -> taskDto.setStatus(Task.TaskStatus.fromCode(s.toCode())));
    taskDto.setOutcome(t.getStatusReason()
        .getText());
    // TODO validate profile and other properties using InstanceValidator
    //Convert ServiceRequest
    String srId = new IdType(t.getFocus()
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
    Organization org = orgMap.get(new IdType(t.getOwner()
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
