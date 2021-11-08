package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class TaskDto {

  private String id;
  private String name;
  private LocalDateTime createdAt;
  private LocalDateTime lastModified;
  private String priority;
  private String status;
  private String statusReason;
  private ServiceRequestDto serviceRequest;
  private TypeDto requester;
  private TypeDto patient;
  private TypeDto performer;
  private TypeDto baseTask;
  private String consent;
  private String outcome;
  private List<CommentDto> comments = new ArrayList<>();
  private List<ProcedureDto> procedures = new ArrayList<>();
}
