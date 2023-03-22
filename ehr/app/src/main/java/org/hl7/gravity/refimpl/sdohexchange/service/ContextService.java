package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.PatientToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.PractitionerToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CurrentContextDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.PatientDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.info.composer.PatientInfoComposer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ContextService {

  private final IGenericClient ehrClient;
  private final PatientInfoComposer patientInfoComposer;
  // TODO: to be removed
  private final String TEST_PATIENT_ID = "smart-1288992";
  private final String TEST_USER_ID = "Smart-Practitioner-71482713";

  public CurrentContextDto getCurrentContext() {
    return new CurrentContextDto(getPatient(), getUser());
  }

  protected PatientDto getPatient() {
    // TODO: to be removed
    // String patientId = SmartOnFhirContext.get()
    // .getPatient();
    String patientId = TEST_PATIENT_ID;
    Assert.notNull(patientId, "Patient id cannot be null.");
    return new PatientToDtoConverter().convert(patientInfoComposer.compose(patientId));
  }

  protected UserDto getUser() {
    // TODO: to be removed
    IdType fhirUser = new IdType("Practitioner/" + TEST_USER_ID);
    // IdType fhirUser = new IdType(SmartOnFhirContext.get()
    // .getFhirUser());
    Assert.isTrue(fhirUser.hasIdPart(), "Current User cannot be null.");
    // TODO support other user Resource types. For example another Patient can
    // create Tasks.
    Assert.isTrue(Practitioner.class.getSimpleName()
        .equals(fhirUser.getResourceType()),
        "Current user is not a Practitioner. Only Practitioner resource type is supported for now.");
    Practitioner practitioner = ehrClient.read()
        .resource(Practitioner.class)
        .withId(fhirUser.getIdPart())
        .execute();
    return new PractitionerToDtoConverter().convert(practitioner);
  }
}
