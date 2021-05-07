package org.hl7.gravity.refimpl.sdohexchange.dto.converter.bundle.response;

import org.hl7.fhir.r4.model.Goal;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHDomainCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Mykhailo Stefantsiv
 */
public class GoalToResponseDtoConverter implements Converter<Goal, GoalResponseDto> {

  @Override
  public GoalResponseDto convert(Goal goal) {
    String id = goal.getIdElement()
        .getIdPart();
    String display = FhirUtil.findCoding(goal.getCategory(), SDOHDomainCode.SYSTEM)
        .getDisplay();
    return new GoalResponseDto(id, display);
  }
}
