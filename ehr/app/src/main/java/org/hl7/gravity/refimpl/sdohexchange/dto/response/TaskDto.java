package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.Validated;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.Priority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class TaskDto implements Validated {

  private final String id;

  private String name;
  private LocalDateTime createdAt;
  private LocalDateTime lastModified;
  private Priority priority;
  private String status;
  private List<CommentDto> comments = new ArrayList<>();
  private String outcome;

  private List<ProcedureResponseDto> procedures = new ArrayList<>();
  private ServiceRequestDto serviceRequest;
  private OrganizationDto organization;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();
}