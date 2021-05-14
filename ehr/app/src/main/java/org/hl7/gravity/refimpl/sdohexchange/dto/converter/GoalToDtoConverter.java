package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Goal;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalDto;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Mykhailo Stefantsiv
 */
public class GoalToDtoConverter implements Converter<Goal, GoalDto> {

  @Override
  public GoalDto convert(Goal goal) {
    String id = goal.getIdElement()
        .getIdPart();
    String display = goal.getDescription()
        .getCodingFirstRep()
        .getDisplay();
    return new GoalDto(id, display);
  }
}
