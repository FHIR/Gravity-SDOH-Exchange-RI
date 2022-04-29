package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.AssessmentDto.AssessmentResponse;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.StringTypeDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.AseessmentInfoBundleExtractor;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This is a secondary converter which generates question-answer pairs based just on items, instead of observations.
 * Using observations is more safe, since sometimes items might not contain the full info, like complete text. But we
 * will keep it for now just in case.
 */
public class AssessmentResponseToDtoConverter2
    implements Converter<AseessmentInfoBundleExtractor.AssessmentInfoHolder, List<AssessmentResponse>> {

  private final TypeToDtoConverter typeToDtoConverter = new TypeToDtoConverter();

  @Override
  public List<AssessmentResponse> convert(AseessmentInfoBundleExtractor.AssessmentInfoHolder infoHolder) {

    return infoHolder.getQuestionnaireResponse()
        .getItem()
        .stream()
        .map(qr -> {
          AssessmentResponse assessmentResponse = new AssessmentResponse();
          assessmentResponse.setQuestion(new StringTypeDto(qr.getText()));
          Type itemAnswer = qr.getAnswerFirstRep()
              .getValue();

          if (itemAnswer instanceof StringType) {
            assessmentResponse.setAnswer(new StringTypeDto(((StringType) itemAnswer).getValue()));
          } else if (itemAnswer instanceof Coding) {
            assessmentResponse.setAnswer(new StringTypeDto(((Coding) itemAnswer).getDisplay()));
          } else {
            assessmentResponse.getErrors()
                .add(String.format("Answer cannot be resolved. %s type is not expected.", itemAnswer.getClass()
                    .getSimpleName()));
            assessmentResponse.setAnswer(new StringTypeDto("Answer cannot be parsed."));
          }
          return assessmentResponse;
        })
        .collect(Collectors.toList());
  }
}