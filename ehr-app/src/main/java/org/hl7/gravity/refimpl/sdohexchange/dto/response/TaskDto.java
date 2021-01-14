package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.TaskCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class TaskDto implements Validated {

  private final String taskId;

  private TaskCode type;
  private Task.TaskStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime lastModified;
  private String outcome;

  private ServiceRequestDto serviceRequest;
  private OrganizationDto organization;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();

}
