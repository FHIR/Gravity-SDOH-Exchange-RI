package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.AssessmentBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.AssessmentDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.query.AssessmentQueryFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class AssessmentService {

  private final IGenericClient ehrClient;
  // TODO: to be removed
  private final String TEST_PATIENT_ID = "smart-1288992";

  public List<AssessmentDto> listCompleted() {
    // TODO: REWORK THIS
    // Assert.notNull(SmartOnFhirContext.get()
    // .getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchAssessmentQuery().where(QuestionnaireResponse.STATUS.exactly()
        .code(QuestionnaireResponseStatus.COMPLETED.toCode()))
        .returnBundle(Bundle.class)
        .execute();
    responseBundle = addQuestionnairesToAssessmentBundle(responseBundle);
    return new AssessmentBundleToDtoConverter().convert(responseBundle);
  }

  public AssessmentDto search(String questionnaireUrl) {
    // TODO: REWORK THIS
    // Assert.notNull(SmartOnFhirContext.get()
    // .getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchAssessmentQuery().where(QuestionnaireResponse.QUESTIONNAIRE.hasId(questionnaireUrl))
        .returnBundle(Bundle.class)
        .execute();

    responseBundle = addQuestionnairesToAssessmentBundle(responseBundle);

    return new AssessmentBundleToDtoConverter().convert(responseBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Resource of type QuestionnaireResponse with " + "questionnaire url '%s' is not known",
                questionnaireUrl)));
  }

  private Bundle addQuestionnairesToAssessmentBundle(Bundle responseBundle) {
    // Extract all 'addresses' references as ids and search for corresponding
    // Conditions, since they cannot be included.
    List<String> urls = FhirUtil.getFromBundle(responseBundle, QuestionnaireResponse.class)
        .stream()
        .map(q -> q.getQuestionnaire())
        .collect(Collectors.toList());

    if (urls.size() != 0) {
      Bundle questionnaires = ehrClient.search()
          .forResource(Questionnaire.class)
          .where(Questionnaire.URL.matches()
              .values(urls))
          .returnBundle(Bundle.class)
          .execute();

      return FhirUtil.mergeBundles(ehrClient.getFhirContext(), responseBundle, questionnaires);
    }
    return responseBundle;
  }

  private IQuery<IBaseBundle> searchAssessmentQuery() {
    // TODO: REWORK THIS to remove the TEST_PATIENT_ID that will be querried
    // return new AssessmentQueryFactory().query(ehrClient, SmartOnFhirContext.get()
    // .getPatient())
    return new AssessmentQueryFactory().query(ehrClient, TEST_PATIENT_ID)
        .revInclude(Observation.INCLUDE_DERIVED_FROM.setRecurse(true))
        .revInclude(Condition.INCLUDE_EVIDENCE_DETAIL.setRecurse(true))
        .sort()
        .descending(QuestionnaireResponse.AUTHORED);
  }
}
