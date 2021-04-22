package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Task;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class TaskDto {

  private String id;
  private String name;
  private LocalDateTime createdAt;
  private LocalDateTime lastModified;
  private Task.TaskPriority priority;
  private Task.TaskStatus status;
  private ServiceRequestDto serviceRequest;
  private TypeDto requester;
  private TypeDto patient;
  private String consent;
  private List<CommentDto> comments;
  private String outcome;
}
