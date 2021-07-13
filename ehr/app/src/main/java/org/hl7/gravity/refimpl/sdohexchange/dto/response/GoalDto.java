package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.codesystems.GoalAchievement;
import org.hl7.gravity.refimpl.sdohexchange.dto.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoalDto implements Validated {

  private String id;
  private String name;
  private GoalAchievement achievementStatus;
  private CodingDto category;
  private CodingDto snomedCode;

  private LocalDate startDate;
  private LocalDate endDate;
  private TypeDto addedBy;
  private List<ConditionDto> problems = new ArrayList<>();
  private List<CommentDto> comments = new ArrayList<>();

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();
}