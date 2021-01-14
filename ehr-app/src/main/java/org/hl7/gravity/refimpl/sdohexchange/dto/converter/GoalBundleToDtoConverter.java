package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.codesystems.GoalAchievement;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.codesystems.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.codesystems.SDOHDomainCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GoalBundleToDtoConverter implements Converter<Bundle, List<GoalDto>> {

  @Override
  public List<GoalDto> convert(Bundle bundle) {

    return FhirUtil.getFromBundle(bundle, Goal.class)
        .stream()
        .map(this::composeGoalDto)
        .collect(Collectors.toList());
  }

  protected GoalDto composeGoalDto(Goal g) {
    GoalDto goalDto = new GoalDto(g.getIdElement()
        .getIdPart());

    goalDto.setLifecycleStatus(g.getLifecycleStatus());
    goalDto.setAchievementStatus(GoalAchievement.fromCode(g.getAchievementStatus()
        .getCodingFirstRep()
        .getCode()));
    Coding coding = FhirUtil.findCoding(g.getCategory(), SDOHDomainCode.SYSTEM);
    if (coding == null) {
      goalDto.getErrors()
          .add(String.format(
              "Goal with id '%s' has no coding with system '%s' within a 'category' property. Such goals "
                  + "are not expected in this context.", g.getIdElement()
                  .getIdPart(), OrganizationTypeCode.SYSTEM));
    } else {
      try {
        goalDto.setDomain(SDOHDomainCode.fromCode(coding.getCode()));
      } catch (FHIRException exc) {
        goalDto.getErrors()
            .add(String.format("SDOHDomainCode code '%s' cannot be resolved for Goal with id '%s'.", coding.getCode(),
                g.getIdElement()
                    .getIdPart()));
      }
    }

    goalDto.setStatusDate(FhirUtil.toLocalDate(g.getStatusDateElement()));

    return goalDto;
  }
}
