package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.TaskCode;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
//TODO: To be implemented
public class TaskDto {

  private String id;
  private Task.TaskPriority priority;
  private Task.TaskStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime lastModified;
  private String outcome;

  private ServiceRequestDto serviceRequest;
  private OrganizationDto organization;
}
