package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.AssessmentDto.AssessmentAnswer;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.AssessmentDto.AssessmentItem;
import org.springframework.core.convert.converter.Converter;

public class AssessmentItemToDtoConverter
    implements Converter<List<QuestionnaireResponseItemComponent>, List<AssessmentItem>> {

  private final TypeToDtoConverter typeToDtoConverter = new TypeToDtoConverter();

  @Override
  public List<AssessmentItem> convert(List<QuestionnaireResponseItemComponent> questionnaireResponseItemComponents) {
    return questionnaireResponseItemComponents.stream()
        .map(itemComponent -> {
          AssessmentItem assessmentItem = new AssessmentItem();
          assessmentItem.setText(itemComponent.getText());
          assessmentItem.setAnswer(itemComponent.getAnswer()
              .stream()
              .map(this::convertAnswer)
              .collect(Collectors.toList()));
          assessmentItem.setItem(convert(itemComponent.getItem()));
          return assessmentItem;
        })
        .collect(Collectors.toList());
  }

  private AssessmentAnswer convertAnswer(QuestionnaireResponseItemAnswerComponent answer){
    AssessmentAnswer assessmentAnswer = new AssessmentAnswer();
    assessmentAnswer.setValue(typeToDtoConverter.convert(answer.getValue()));
    return assessmentAnswer;
  }
}