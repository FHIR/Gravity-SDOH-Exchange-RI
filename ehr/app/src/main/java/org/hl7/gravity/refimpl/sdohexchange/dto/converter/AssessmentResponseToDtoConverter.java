package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.AssessmentDto.AssessmentResponse;
import org.springframework.core.convert.converter.Converter;

public class AssessmentResponseToDtoConverter implements Converter<List<Observation>, List<AssessmentResponse>> {

  private final TypeToDtoConverter typeToDtoConverter = new TypeToDtoConverter();

  @Override
  public List<AssessmentResponse> convert(List<Observation> observations) {
    return observations.stream()
        .filter(Observation::hasValue)
        .map(this::convertResponse)
        .collect(Collectors.toList());
  }

  private AssessmentResponse convertResponse(Observation observation) {
    AssessmentResponse assessmentResponse = new AssessmentResponse();
    assessmentResponse.setQuestion(typeToDtoConverter.convert(observation.getCode()));
    assessmentResponse.setAnswer(typeToDtoConverter.convert(observation.getValue()));
    return assessmentResponse;
  }
}