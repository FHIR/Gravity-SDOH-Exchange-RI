package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import static org.hl7.gravity.refimpl.sdohexchange.dto.converter.AssessmentInfoToDtoConverter.QUESTIONNAIRE_NAME_EXTENSION;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.System;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.HealthConcernDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.StringTypeDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.HealthConcernInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

public class HealthConcernBundleToDtoConverter implements Converter<Bundle, List<HealthConcernDto>> {

  private final HealthConcernInfoBundleExtractor healthConcernInfoBundleExtractor =
      new HealthConcernInfoBundleExtractor();
  private final CodeableConceptToStringConverter codeableConceptToStringConverter =
      new CodeableConceptToStringConverter(", ");

  @Override
  public List<HealthConcernDto> convert(Bundle bundle) {
    return healthConcernInfoBundleExtractor.extract(bundle)
        .stream()
        .map(healthConcernInfo -> {
          HealthConcernDto healthConcernDto = new HealthConcernDto();
          Condition condition = healthConcernInfo.getCondition();
          healthConcernDto.setId(condition.getIdElement()
              .getIdPart());
          healthConcernDto.setName(codeableConceptToStringConverter.convert(condition.getCode()));
          Coding categoryCoding = FhirUtil.findCoding(condition.getCategory(), SDOHMappings.getInstance()
              .getSystem());
          if (categoryCoding == null) {
            healthConcernDto.getErrors()
                .add("SDOH category is not found.");
          } else {
            healthConcernDto.setCategory(new CodingDto(categoryCoding.getCode(), categoryCoding.getDisplay()));
          }
          Optional<Coding> icdCodeCodingOptional = findCode(condition.getCode(), System.ICD_10);
          if (icdCodeCodingOptional.isPresent()) {
            Coding icdCodeCoding = icdCodeCodingOptional.get();
            healthConcernDto.setIcdCode(new CodingDto(icdCodeCoding.getCode(), icdCodeCoding.getDisplay()));
          } else {
            healthConcernDto.getErrors()
                .add("ICD-10 code is not found.");
          }
          Optional<Coding> snomedCodeCodingOptional = findCode(condition.getCode(), System.SNOMED);
          if (snomedCodeCodingOptional.isPresent()) {
            Coding snomedCodeCoding = snomedCodeCodingOptional.get();
            healthConcernDto.setSnomedCode(new CodingDto(snomedCodeCoding.getCode(), snomedCodeCoding.getDisplay()));
          } else {
            healthConcernDto.getErrors()
                .add("SNOMED-CT code is not found.");
          }

          QuestionnaireResponse questionnaireResponse = healthConcernInfo.getQuestionnaireResponse();
          if (questionnaireResponse != null) {
            healthConcernDto.setDate(FhirUtil.toLocalDateTime(questionnaireResponse.getAuthoredElement()));
            CanonicalType questionnaireElement = questionnaireResponse.getQuestionnaireElement();
            Optional<String> questionnaireName =
                Optional.ofNullable(questionnaireElement.getExtensionString(QUESTIONNAIRE_NAME_EXTENSION));
            if (questionnaireName.isPresent()) {
              healthConcernDto.setBasedOn(new ReferenceDto(questionnaireResponse.getIdElement()
                  .getIdPart(), questionnaireName.get()));
            } else {
              healthConcernDto.getErrors()
                  .add("Based on QuestionnaireResponse doesn't have questionnaire name extension.");
            }
          } else {
            healthConcernDto.setBasedOn(new StringTypeDto(condition.getEvidenceFirstRep()
                .getCodeFirstRep()
                .getText()));
            Reference recorder = condition.getRecorder();
            healthConcernDto.setAuthoredBy(new ReferenceDto(recorder.getReferenceElement()
                .getIdPart(), recorder.getDisplay()));
          }
          return healthConcernDto;
        })
        .collect(Collectors.toList());
  }

  private Optional<Coding> findCode(CodeableConcept code, String system) {
    return code.getCoding()
        .stream()
        .filter(coding -> coding.getSystem()
            .equals(system))
        .findFirst();
  }
}