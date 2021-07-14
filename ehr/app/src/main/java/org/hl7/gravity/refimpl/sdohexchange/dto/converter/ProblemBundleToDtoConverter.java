package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalInfoDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ProblemDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskInfoDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.ConditionInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.ProblemInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

import java.util.stream.Collectors;

public class ProblemBundleToDtoConverter extends ConditionBundleToDtoConverterBase<ProblemDto> {

  @Override
  protected ProblemDto conditionInfoToDto(ConditionInfoBundleExtractor.ConditionInfoHolder conditionInfo) {
    //TODO refactor this. Avoid manual casting. Refactor the design of a base class instead.
    Assert.isInstanceOf(ProblemInfoBundleExtractor.ProblemInfoHolder.class, conditionInfo,
        "conditionInfo must be a ProblemInfoHolder.");
    ProblemInfoBundleExtractor.ProblemInfoHolder probleminfo =
        (ProblemInfoBundleExtractor.ProblemInfoHolder) conditionInfo;
    Condition condition = probleminfo.getCondition();

    ProblemDto problemDto = super.conditionInfoToDto(probleminfo);
    //Onset must be available for the problem list items.
    if (condition.getOnset() != null) {
      problemDto.setStartDate(FhirUtil.toLocalDateTime((DateTimeType) condition.getOnset()));
    } else {
      problemDto.getErrors()
          .add("Condition is a problem-list-item but an onset property is missing or not of a DateTimeType " + "type.");
    }
    problemDto.getTasks()
        .addAll(probleminfo.getTasks()
            .stream()
            .map(t -> new TaskInfoDto(t.getId(), t.getName(), t.getStatus()))
            .collect(Collectors.toList()));

    problemDto.getGoals()
        .addAll(probleminfo.getGoals()
            .stream()
            .map(t -> new GoalInfoDto(t.getId(), t.getName(), t.getStatus()))
            .collect(Collectors.toList()));
    return problemDto;
  }

  protected ProblemDto newConditionDtoImpl() {
    return new ProblemDto();
  }

  @Override
  protected ConditionInfoBundleExtractor newConditionBundleExtractor() {
    return new ProblemInfoBundleExtractor();
  }
}