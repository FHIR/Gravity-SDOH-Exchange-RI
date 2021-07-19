package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Goal;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalInfoDto;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Mykhailo Stefantsiv
 */
public class GoalToInfoDtoConverter implements Converter<Goal, GoalInfoDto> {

  @Override
  public GoalInfoDto convert(Goal goal) {
    String id = goal.getIdElement()
        .getIdPart();
    String display = null;
    if (goal.hasDescription() && goal.getDescription()
        .hasText()) {
      display = goal.getDescription()
          .getText();
    }
    return new GoalInfoDto(id, display, goal.getLifecycleStatus());
  }
}
