package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConditionDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

public class GoalBundleToDtoConverter implements Converter<Bundle, List<GoalDto>> {

  private final AnnotationToDtoConverter annotationToDtoConverter = new AnnotationToDtoConverter();
  private final CodeableConceptToStringConverter codeableConceptToStringConverter =
      new CodeableConceptToStringConverter(", ");

  @Override
  public List<GoalDto> convert(Bundle bundle) {
    return FhirUtil.getFromBundle(bundle, Goal.class)
        .stream()
        .map(goal -> {
          GoalDto goalDto = new GoalDto();
          goalDto.setId(goal.getIdElement()
              .getIdPart());
          goalDto.setName(codeableConceptToStringConverter.convert(goal.getDescription()));

          //TODO reused from HealthConcernBundleToDtoConverter class. Refactor!
          Coding categoryCoding = FhirUtil.findCoding(goal.getCategory(), SDOHMappings.getInstance()
              .getSystem());
          // TODO remove this in future. Fow now two different categories might be used before discussed.
          if (categoryCoding == null) {
            categoryCoding = FhirUtil.findCoding(goal.getCategory(),
                "http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/SDOHCC-CodeSystemTemporaryCodes");
          }
          if (categoryCoding == null) {
            goalDto.getErrors()
                .add("SDOH category is not found.");
          } else {
            goalDto.setCategory(new CodingDto(categoryCoding.getCode(), categoryCoding.getDisplay()));
          }

          //TODO this is invalid. To clarify!
          Coding code = goal.getDescription()
              .getCodingFirstRep();
          goalDto.setSnomedCode(new CodingDto(code.getCode(), code.getDisplay()));

          if (goal.hasExpressedBy()) {
            Reference expressedBy = goal.getExpressedBy();
            goalDto.setAddedBy(new ReferenceDto(expressedBy.getReferenceElement()
                .getIdPart(), expressedBy.getDisplay()));
          } else {
            goalDto.getErrors()
                .add("Goal expressedBy property is missing. addedBy will be null.");
          }

          goalDto.setStartDate(FhirUtil.toLocalDate(goal.getStartDateType()));
          //TODO this is invalid. To clarify!
          if (goal.getLifecycleStatus() == Goal.GoalLifecycleStatus.COMPLETED) {
            if (goal.hasStatusDate()) {
              goalDto.setEndDate(FhirUtil.toLocalDate(goal.getStatusDateElement()));
            } else {
              goalDto.getErrors()
                  .add("Goal statusDate must be set when the lifecycleStatus is COMPLETED. endDate will be null.");
            }
          }

          goalDto.setComments(goal.getNote()
              .stream()
              .map(annotationToDtoConverter::convert)
              .collect(Collectors.toList()));

          goalDto.setProblems(goal.getAddresses()
              .stream()
              .filter(ref -> Condition.class.getSimpleName()
                  .equals(ref.getReferenceElement()
                      .getResourceType()))
              .map(ref -> (Condition) ref.getResource())
              .map(cond -> new ConditionDto(cond.getIdElement()
                  .getIdPart(), codeableConceptToStringConverter.convert(cond.getCode())))
              .collect(Collectors.toList()));
          return goalDto;
        })
        .collect(Collectors.toList());
  }
}