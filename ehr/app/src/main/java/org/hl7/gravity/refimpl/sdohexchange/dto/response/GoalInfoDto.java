package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.r4.model.Goal;

/**
 * Basic goal info for goal selection drop down.
 */
@Getter
@AllArgsConstructor
public class GoalInfoDto {

  private String id;
  private String display;
  private Goal.GoalLifecycleStatus status;
}
