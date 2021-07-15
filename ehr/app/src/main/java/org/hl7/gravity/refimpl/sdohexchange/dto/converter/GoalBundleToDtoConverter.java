package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.codesystems.GoalAchievement;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.System;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConditionDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Optional;
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
          if (goal.hasDescription() && goal.getDescription()
              .hasText()) {
            goalDto.setName(goal.getDescription()
                .getText());
          } else {
            goalDto.getErrors()
                .add("Goal description.text not found. Name cannot be set.");
          }

          if (goal.hasAchievementStatus()) {
            goalDto.setAchievementStatus(GoalAchievement.fromCode(goal.getAchievementStatus()
                .getCodingFirstRep()
                .getCode()));
          } else {
            goalDto.getErrors()
                .add("No achievement status available.");
          }

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

          Optional<Coding> snomedCodeCodingOptional = findCode(goal.getDescription(), System.SNOMED);
          if (snomedCodeCodingOptional.isPresent()) {
            Coding snomedCodeCoding = snomedCodeCodingOptional.get();
            goalDto.setSnomedCode(new CodingDto(snomedCodeCoding.getCode(), snomedCodeCoding.getDisplay()));
          } else {
            goalDto.getErrors()
                .add("SNOMED-CT code is not found.");
          }

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

  //TODO copied from HealthConcernBundleToDtoConverter
  private Optional<Coding> findCode(CodeableConcept code, String system) {
    return code.getCoding()
        .stream()
        .filter(coding -> coding.getSystem()
            .equals(system))
        .findFirst();
  }
}