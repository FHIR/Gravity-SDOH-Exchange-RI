package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.AssessmentDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.AseessmentInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.AseessmentInfoBundleExtractor.AssessmentInfoHolder;
import org.springframework.core.convert.converter.Converter;

public class AssessmentBundleToDtoConverter implements Converter<Bundle, List<AssessmentDto>> {

  private final AseessmentInfoBundleExtractor bundleExtractor = new AseessmentInfoBundleExtractor();
  private final AssessmentInfoToDtoConverter assessmentInfoToDtoConverter = new AssessmentInfoToDtoConverter();

  @Override
  public List<AssessmentDto> convert(Bundle bundle) {
    List<AssessmentInfoHolder> infoHolders = bundleExtractor.extract(bundle);
    // Grouped by questionnaire name resources. Each group is a questionnaire response history
    Map<String, List<AssessmentInfoHolder>> questionnaireHistory = infoHolders.stream()
        .collect(Collectors.groupingBy(info -> info.getQuestionnaireResponse()
            .getQuestionnaire()));
    return questionnaireHistory.values()
        .stream()
        .map(historyValue -> {
          Collections.sort(historyValue);
          AssessmentInfoHolder latestQuestionnaireInfo = historyValue
              .get(0);
          AssessmentDto latestQuestionnaireResponse =
              assessmentInfoToDtoConverter.convert(latestQuestionnaireInfo);

          latestQuestionnaireResponse.setPrevious(historyValue.stream()
              .skip(1)
              .map(assessmentInfoToDtoConverter::convert)
              .collect(Collectors.toList()));
          return latestQuestionnaireResponse;
        })
        .collect(Collectors.toList());
  }
}
