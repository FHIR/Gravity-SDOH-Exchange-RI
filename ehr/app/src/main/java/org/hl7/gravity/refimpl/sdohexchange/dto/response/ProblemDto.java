package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDto extends ConditionDtoBase {

  private LocalDateTime startDate;
  private List<TaskInfoDto> tasks = new ArrayList<>();
  private List<GoalInfoDto> goals = new ArrayList<>();
}