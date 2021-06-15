package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.AssessmentBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.AssessmentDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class AssessmentService {

  private final SmartOnFhirContext smartOnFhirContext;
  private final IGenericClient ehrClient;

  public List<AssessmentDto> listCompleted() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = ehrClient.search()
        .forResource(QuestionnaireResponse.class)
        .where(QuestionnaireResponse.PATIENT.hasId(smartOnFhirContext.getPatient()))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.QUESTIONNAIRE_RESPONSE))
        .where(QuestionnaireResponse.STATUS.exactly()
            .code(QuestionnaireResponseStatus.COMPLETED.toCode()))
        .revInclude(Observation.INCLUDE_DERIVED_FROM)
        .revInclude(Condition.INCLUDE_EVIDENCE_DETAIL.setRecurse(true))
        .returnBundle(Bundle.class)
        .execute();
    return new AssessmentBundleToDtoConverter().convert(responseBundle);
  }

  public AssessmentDto search(String questionnaireUrl) {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = ehrClient.search()
        .forResource(QuestionnaireResponse.class)
        .where(QuestionnaireResponse.PATIENT.hasId(smartOnFhirContext.getPatient()))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.QUESTIONNAIRE_RESPONSE))
        .where(QuestionnaireResponse.QUESTIONNAIRE.hasId(questionnaireUrl))
        .revInclude(Observation.INCLUDE_DERIVED_FROM)
        .revInclude(Condition.INCLUDE_EVIDENCE_DETAIL.setRecurse(true))
        .returnBundle(Bundle.class)
        .execute();
    return new AssessmentBundleToDtoConverter().convert(responseBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(String.format("Resource of type QuestionnaireResponse with "
            + "questionnaire url '%s' is not known", questionnaireUrl)));
  }
}
