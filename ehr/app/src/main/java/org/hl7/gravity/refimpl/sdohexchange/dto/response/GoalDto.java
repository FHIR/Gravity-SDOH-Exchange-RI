package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.codesystems.GoalAchievement;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHDomainCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class GoalDto implements Validated {

  private final String id;
  private Goal.GoalLifecycleStatus lifecycleStatus;
  private GoalAchievement achievementStatus;
  //TODO support category
  //private String category;
  private SDOHDomainCode domain;
  private LocalDate statusDate;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();
}
