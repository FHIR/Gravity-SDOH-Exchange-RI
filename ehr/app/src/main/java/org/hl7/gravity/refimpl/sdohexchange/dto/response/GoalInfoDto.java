package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Basic goal info for goal selection drop down.
 */
@Getter
@AllArgsConstructor
public class GoalInfoDto {

  private String id;
  private String display;
}
