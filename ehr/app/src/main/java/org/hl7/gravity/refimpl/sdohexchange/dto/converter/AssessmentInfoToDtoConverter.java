package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import java.util.Optional;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.AssessmentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.AseessmentInfoBundleExtractor.AssessmentInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

public class AssessmentInfoToDtoConverter
    implements Converter<AssessmentInfoHolder, AssessmentDto> {

  public static final String QUESTIONNAIRE_NAME_EXTENSION = "http://hl7.org/fhir/StructureDefinition/display";

  private final AssessmentResponseToDtoConverter
      assessmentResponseToDtoConverter = new AssessmentResponseToDtoConverter();
  private final CodeableConceptToStringConverter codeableConceptToStringConverter =
      new CodeableConceptToStringConverter(", ");

  @Override
  public AssessmentDto convert(AssessmentInfoHolder infoHolder) {
    AssessmentDto assessmentDto = new AssessmentDto();
    QuestionnaireResponse questionnaireResponse = infoHolder.getQuestionnaireResponse();
    assessmentDto.setId(questionnaireResponse.getIdElement()
        .getIdPart());

    CanonicalType questionnaireElement = questionnaireResponse.getQuestionnaireElement();
    Optional<String> questionnaireName =
        Optional.ofNullable(questionnaireElement.getExtensionString(QUESTIONNAIRE_NAME_EXTENSION));
    if (questionnaireName.isPresent()) {
      assessmentDto.setName(questionnaireName.get());
    } else {
      assessmentDto.getErrors()
          .add("QuestionnaireResponse doesn't have questionnaire name extension.");
    }
    assessmentDto.setQuestionnaireUrl(questionnaireResponse.getQuestionnaire());
    assessmentDto.setDate(FhirUtil.toLocalDateTime(questionnaireResponse
        .getAuthoredElement()));
    assessmentDto.setAssessmentResponse(assessmentResponseToDtoConverter.convert(infoHolder.getObservations()));

    assessmentDto.setHealthConcerns(infoHolder.getConditions()
        .stream()
        .map(condition -> new ReferenceDto(condition.getIdElement()
            .getIdPart(), codeableConceptToStringConverter.convert(condition.getCode())))
        .collect(Collectors.toList()));
    return assessmentDto;
  }
}
