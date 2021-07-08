package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.System;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.HealthConcernDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.StringTypeDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.ConditionClinicalStatusCodes;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCoreConditionCategory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.ConditionInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hl7.gravity.refimpl.sdohexchange.dto.converter.AssessmentInfoToDtoConverter.QUESTIONNAIRE_NAME_EXTENSION;

public class HealthConcernBundleToDtoConverter implements Converter<Bundle, List<HealthConcernDto>> {

  private final ConditionInfoBundleExtractor conditionInfoBundleExtractor = new ConditionInfoBundleExtractor();
  private final CodeableConceptToStringConverter codeableConceptToStringConverter =
      new CodeableConceptToStringConverter(", ");

  @Override
  public List<HealthConcernDto> convert(Bundle bundle) {
    return conditionInfoBundleExtractor.extract(bundle)
        .stream()
        .map(healthConcernInfo -> {
          HealthConcernDto healthConcernDto = new HealthConcernDto();
          Condition condition = healthConcernInfo.getCondition();
          healthConcernDto.setId(condition.getIdElement()
              .getIdPart());
          healthConcernDto.setName(codeableConceptToStringConverter.convert(condition.getCode()));
          Coding categoryCoding = FhirUtil.findCoding(condition.getCategory(), SDOHMappings.getInstance()
              .getSystem());
          // TODO remove this in future. Fow now two different categories might be used before discussed.
          if (categoryCoding == null) {
            categoryCoding = FhirUtil.findCoding(condition.getCategory(),
                "http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/SDOHCC-CodeSystemTemporaryCodes");
          }
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
            healthConcernDto.setAssessmentDate(FhirUtil.toLocalDateTime(questionnaireResponse.getAuthoredElement()));
            CanonicalType questionnaireElement = questionnaireResponse.getQuestionnaireElement();
            Optional<String> questionnaireName = Optional.ofNullable(
                questionnaireElement.getExtensionString(QUESTIONNAIRE_NAME_EXTENSION));
            if (questionnaireName.isPresent()) {
              healthConcernDto.setBasedOn(new ReferenceDto(questionnaireResponse.getIdElement()
                  .getIdPart(), questionnaireName.get()));
            } else {
              // If extension is not set - use ID instead.
              healthConcernDto.setBasedOn(new ReferenceDto(questionnaireResponse.getIdElement()
                  .getIdPart(), questionnaireElement.getValueAsString()));
              healthConcernDto.getErrors()
                  .add("Based on QuestionnaireResponse doesn't have questionnaire name extension. Using ID instead.");
            }
          } else {
            healthConcernDto.setBasedOn(new StringTypeDto(condition.getEvidenceFirstRep()
                .getCodeFirstRep()
                .getText()));
            Reference recorder = condition.getRecorder();
            healthConcernDto.setAuthoredBy(new ReferenceDto(recorder.getReferenceElement()
                .getIdPart(), recorder.getDisplay()));
          }

          // abatement must be available for all resolved condition
          if (ConditionClinicalStatusCodes.RESOLVED.toCode()
              .equals(condition.getClinicalStatus()
                  .getCodingFirstRep()
                  .getCode())) {
            if (condition.getAbatement() instanceof DateTimeType) {
              healthConcernDto.setResolutionDate(FhirUtil.toLocalDateTime((DateTimeType) condition.getAbatement()));
            } else {
              healthConcernDto.getErrors()
                  .add("Condition is resolved but an abatement property is missing or not of a DateTimeType type.");
            }
          }

          //Onset must be available for the problem list items.
          Coding category = FhirUtil.findCoding(condition.getCategory(),
              UsCoreConditionCategory.PROBLEMLISTITEM.getSystem());
          if (category != null && UsCoreConditionCategory.PROBLEMLISTITEM.toCode()
              .equals(category.getCode())) {
            if (condition.getOnset() != null) {
              healthConcernDto.setStartDate(FhirUtil.toLocalDateTime((DateTimeType) condition.getOnset()));
            } else {
              healthConcernDto.getErrors()
                  .add("Condition is a problem-list-item but an onset property is missing or not of a DateTimeType "
                      + "type.");
            }
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