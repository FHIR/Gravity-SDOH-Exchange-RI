package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.gravity.refimpl.sdohexchange.sdohmappings.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.sdohmappings.System;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConditionDtoBase;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.StringTypeDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.ConditionClinicalStatusCodes;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.ConditionInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//TODO refactor this class. It is very poorly designed.
public abstract class ConditionBundleToDtoConverterBase<T extends ConditionDtoBase>
    implements Converter<Bundle, List<T>> {

  private final CodeableConceptToStringConverter codeableConceptToStringConverter =
      new CodeableConceptToStringConverter(", ");

  @Override
  public final List<T> convert(Bundle bundle) {
    return newConditionBundleExtractor().extract(bundle)
        .stream()
        .map(conditionInfo -> {
          return conditionInfoToDto(conditionInfo);
        })
        .collect(Collectors.toList());
  }

  protected T conditionInfoToDto(ConditionInfoBundleExtractor.ConditionInfoHolder conditionInfo) {
    T conditionDto = newConditionDtoImpl();
    Condition condition = conditionInfo.getCondition();
    conditionDto.setId(condition.getIdElement()
        .getIdPart());
    conditionDto.setName(codeableConceptToStringConverter.convert(condition.getCode()));
    Coding categoryCoding = FhirUtil.findCoding(condition.getCategory(), SDOHMappings.getInstance()
        .getSystem());
    // TODO remove this in future. Fow now two different categories might be used before discussed.
    if (categoryCoding == null) {
      categoryCoding = FhirUtil.findCoding(condition.getCategory(),
          "http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/SDOHCC-CodeSystemTemporaryCodes");
    }
    if (categoryCoding == null) {
      conditionDto.getErrors()
          .add("SDOH category is not found.");
    } else {
      conditionDto.setCategory(new CodingDto(categoryCoding.getCode(), categoryCoding.getDisplay()));
    }
    Optional<Coding> icdCodeCodingOptional = findCode(condition.getCode(), System.ICD_10);
    if (icdCodeCodingOptional.isPresent()) {
      Coding icdCodeCoding = icdCodeCodingOptional.get();
      conditionDto.setIcdCode(new CodingDto(icdCodeCoding.getCode(), icdCodeCoding.getDisplay()));
    } else {
      conditionDto.getErrors()
          .add("ICD-10 code is not found.");
    }
    Optional<Coding> snomedCodeCodingOptional = findCode(condition.getCode(), System.SNOMED);
    if (snomedCodeCodingOptional.isPresent()) {
      Coding snomedCodeCoding = snomedCodeCodingOptional.get();
      conditionDto.setSnomedCode(new CodingDto(snomedCodeCoding.getCode(), snomedCodeCoding.getDisplay()));
    } else {
      conditionDto.getErrors()
          .add("SNOMED-CT code is not found.");
    }

    QuestionnaireResponse questionnaireResponse = conditionInfo.getQuestionnaireResponse();
    if (questionnaireResponse != null) {
      conditionDto.setAssessmentDate(FhirUtil.toLocalDateTime(questionnaireResponse.getAuthoredElement()));
      Questionnaire questionnaire = conditionInfo.getQuestionnaire();
      if (questionnaire != null) {
        conditionDto.setBasedOn(new ReferenceDto(questionnaireResponse.getIdElement()
            .getIdPart(), questionnaire.getTitle()));
      } else {
        conditionDto.setBasedOn(new ReferenceDto(questionnaireResponse.getIdElement()
            .getIdPart(), questionnaireResponse.getQuestionnaire()));
        conditionDto.getErrors()
            .add("Based on QuestionnaireResponse doesn't have a matching Questionnaire to get a title from. Using URL "
                + "instead.");
      }
    } else {
      conditionDto.setBasedOn(new StringTypeDto(condition.getEvidenceFirstRep()
          .getCodeFirstRep()
          .getText()));
      Reference recorder = condition.getRecorder();
      conditionDto.setAuthoredBy(new ReferenceDto(recorder.getReferenceElement()
          .getIdPart(), recorder.getDisplay()));
    }

    // abatement must be available for all resolved condition
    if (ConditionClinicalStatusCodes.RESOLVED.toCode()
        .equals(condition.getClinicalStatus()
            .getCodingFirstRep()
            .getCode())) {
      if (condition.getAbatement() instanceof DateTimeType) {
        conditionDto.setResolutionDate(FhirUtil.toLocalDateTime((DateTimeType) condition.getAbatement()));
      } else {
        conditionDto.getErrors()
            .add("Condition is resolved but an abatement property is missing or not of a DateTimeType type.");
      }
    }

    return conditionDto;
  }

  protected abstract T newConditionDtoImpl();

  //TODO refactor this
  protected ConditionInfoBundleExtractor newConditionBundleExtractor() {
    return new ConditionInfoBundleExtractor();
  }

  private Optional<Coding> findCode(CodeableConcept code, String system) {
    return code.getCoding()
        .stream()
        .filter(coding -> coding.getSystem()
            .equals(system))
        .findFirst();
  }
}