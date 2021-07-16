package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.AssessmentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.AseessmentInfoBundleExtractor.AssessmentInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.stream.Collectors;

public class AssessmentInfoToDtoConverter implements Converter<AssessmentInfoHolder, AssessmentDto> {

  public static final String QUESTIONNAIRE_NAME_EXTENSION = "http://hl7.org/fhir/StructureDefinition/display";

  private final AssessmentResponseToDtoConverter assessmentResponseToDtoConverter =
      new AssessmentResponseToDtoConverter();
  private final CodeableConceptToStringConverter codeableConceptToStringConverter =
      new CodeableConceptToStringConverter(", ");
  private final TypeToDtoConverter typeToDtoConverter = new TypeToDtoConverter();

  @Override
  public AssessmentDto convert(AssessmentInfoHolder infoHolder) {
    AssessmentDto assessmentDto = new AssessmentDto();
    QuestionnaireResponse questionnaireResponse = infoHolder.getQuestionnaireResponse();
    assessmentDto.setId(questionnaireResponse.getIdElement()
        .getIdPart());

    if (infoHolder.getQuestionnaire() != null) {
      assessmentDto.setName(infoHolder.getQuestionnaire()
          .getTitle());
    } else {
      assessmentDto.setName(questionnaireResponse.getQuestionnaire());
      assessmentDto.getErrors()
          .add("QuestionnaireResponse references a Questionnaire by the URL which does not exist. Using the URL as a "
              + "name instead.");
    }

    assessmentDto.setQuestionnaireUrl(questionnaireResponse.getQuestionnaire());
    assessmentDto.setDate(FhirUtil.toLocalDateTime(questionnaireResponse.getAuthoredElement()));

    assessmentDto.setAssessmentResponse(assessmentResponseToDtoConverter.convert(infoHolder));

    assessmentDto.setHealthConcerns(infoHolder.getConditions()
        .stream()
        .map(condition -> new ReferenceDto(condition.getIdElement()
            .getIdPart(), codeableConceptToStringConverter.convert(condition.getCode())))
        .collect(Collectors.toList()));
    return assessmentDto;
  }
}
