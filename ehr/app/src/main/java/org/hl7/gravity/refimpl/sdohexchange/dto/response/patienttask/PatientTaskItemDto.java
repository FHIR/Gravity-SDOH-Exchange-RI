package org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.gravity.refimpl.sdohexchange.dto.Validated;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.patienttask.PatientTaskType;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientTaskItemDto implements Validated {

  private String id;
  private String name;
  private String priority;
  private PatientTaskType type;
  private String status;
  private String statusReason;
  private LocalDateTime lastModified;
  private CodingDto code;
  private ReferenceDto referralTask;
  private ReferenceDto assessment;
  private String outcome;
  private ReferenceDto assessmentResponse;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();
}
