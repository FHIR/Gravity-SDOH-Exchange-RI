package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Copied from cp/app/src/main/java/org/hl7/gravity/refimpl/sdohexchange/dto/response/
@Getter
@Setter
@RequiredArgsConstructor
public class TaskDto {

  private Integer serverId;
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
  private String consent;
  private String outcome;
  private List<CommentDto> comments = new ArrayList<>();
  private List<ProcedureDto> procedures = new ArrayList<>();
}
