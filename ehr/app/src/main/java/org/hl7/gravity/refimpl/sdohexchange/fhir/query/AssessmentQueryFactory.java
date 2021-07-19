package org.hl7.gravity.refimpl.sdohexchange.fhir.query;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import lombok.AllArgsConstructor;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;

@AllArgsConstructor
public class AssessmentQueryFactory implements FHIRQueryFactory {

  @Override
  public IQuery<IBaseBundle> query(IGenericClient client, String patientId) {
    return client.search()
        .forResource(QuestionnaireResponse.class)
        .where(QuestionnaireResponse.PATIENT.hasId(patientId))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.QUESTIONNAIRE_RESPONSE));
  }
}
