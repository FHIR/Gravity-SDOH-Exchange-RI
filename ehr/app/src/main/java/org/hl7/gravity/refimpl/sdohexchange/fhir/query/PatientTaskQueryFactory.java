package org.hl7.gravity.refimpl.sdohexchange.fhir.query;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import lombok.AllArgsConstructor;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;

@AllArgsConstructor
public class PatientTaskQueryFactory implements FHIRQueryFactory {

  @Override
  public IQuery<IBaseBundle> query(IGenericClient client, String patientId) {
    return client.search()
        .forResource(Task.class)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Task.PATIENT.hasId(patientId))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.PATIENT_TASK));
  }
}
