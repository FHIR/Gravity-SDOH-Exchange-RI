package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Observation;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.AssessmentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.AssessmentDto.AssessmentResponse;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.StringTypeDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.AseessmentInfoBundleExtractor;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssessmentResponseToDtoConverter
    implements Converter<AseessmentInfoBundleExtractor.AssessmentInfoHolder, List<AssessmentResponse>> {

  private final TypeToDtoConverter typeToDtoConverter = new TypeToDtoConverter();

  @Override
  public List<AssessmentResponse> convert(AseessmentInfoBundleExtractor.AssessmentInfoHolder infoHolder) {
    Map<String, String> items = new HashMap<>();
    infoHolder.getQuestionnaireResponse()
        .getItem()
        .forEach(i -> items.put(i.getLinkId(), i.getText()));
    return infoHolder.getObservations()
        .stream()
        .filter(Observation::hasValue)
        .map(o -> {
          AssessmentDto.AssessmentResponse assessmentResponse = new AssessmentDto.AssessmentResponse();
          String code = o.getCode()
              .getCodingFirstRep()
              .getCode();
          String question = items.get("/" + code);
          if (question == null) {
            question = code;
            assessmentResponse.getErrors()
                .add("No question text found. Using a code instead.");
          }
          assessmentResponse.setQuestion(new StringTypeDto(question));
          assessmentResponse.setAnswer(typeToDtoConverter.convert(o.getValue()));
          return assessmentResponse;
        })
        .collect(Collectors.toList());
  }
}