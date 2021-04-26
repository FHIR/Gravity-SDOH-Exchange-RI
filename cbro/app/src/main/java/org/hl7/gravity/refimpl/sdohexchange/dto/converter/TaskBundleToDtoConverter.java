package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ServiceRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class TaskBundleToDtoConverter implements Converter<Bundle, List<TaskDto>> {

  private final TaskToDtoConverter taskToDtoConverter = new TaskToDtoConverter();
  private final ServiceRequestToDtoConverter serviceRequestToDtoConverter = new ServiceRequestToDtoConverter();

  @Override
  public List<TaskDto> convert(Bundle bundle) {
    // Retrieve all Task.focus ServiceRequest instances
    Map<String, ServiceRequest> serviceRequests = FhirUtil.getFromBundle(bundle, ServiceRequest.class)
        .stream()
        .collect(Collectors.toMap(serviceRequest -> serviceRequest.getIdElement()
            .getIdPart(), Function.identity()));

    return FhirUtil.getFromBundle(bundle, Task.class)
        .stream()
        .map(task -> {
          TaskDto taskDto = taskToDtoConverter.convert(task);
          String serviceRequestId = new IdType(task.getFocus()
              .getReference()).toUnqualifiedVersionless()
              .getIdPart();
          ServiceRequest serviceRequest = serviceRequests.get(serviceRequestId);
          ServiceRequestDto serviceRequestDto = serviceRequestToDtoConverter.convert(serviceRequest);
          taskDto.setServiceRequest(serviceRequestDto);
          return taskDto;
        })
        .collect(Collectors.toList());
  }
}