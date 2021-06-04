package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hl7.gravity.refimpl.sdohexchange.dto.Validated;

@Getter
@Setter
public class TaskDto implements Validated {

  private String id;
  private String name;
  private LocalDateTime createdAt;
  private LocalDateTime lastModified;
  private String priority;
  private String status;
  private String statusReason;
  private String outcome;
  private List<CommentDto> comments = new ArrayList<>();
  private List<ProcedureDto> procedures = new ArrayList<>();
  private ServiceRequestDto serviceRequest;
  private TypeDto organization;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();
}