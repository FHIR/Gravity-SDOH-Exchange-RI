package org.hl7.gravity.refimpl.sdohexchange.dto.converter.bundle.response;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHDomainCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mykhailo Stefantsiv
 */
public class GoalBundleToResponseDtoConverter implements Converter<Bundle, List<GoalResponseDto>> {

  @Override
  public List<GoalResponseDto> convert(Bundle bundle) {
    return FhirUtil.getFromBundle(bundle, Goal.class)
        .stream()
        .map(this::composeGoalDto)
        .collect(Collectors.toList());
  }

  protected GoalResponseDto composeGoalDto(Goal goal) {
    String id = goal.getIdElement()
        .getIdPart();
    String display = FhirUtil.findCoding(goal.getCategory(), SDOHDomainCode.SYSTEM)
        .getDisplay();
    return new GoalResponseDto(id, display);
  }
}
