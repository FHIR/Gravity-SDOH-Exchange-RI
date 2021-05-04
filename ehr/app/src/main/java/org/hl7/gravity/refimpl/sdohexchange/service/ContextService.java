package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.PatientToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.PractitionerToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CurrentContextDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.PatientDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ContextService {

  private final IGenericClient ehrClient;
  private final SmartOnFhirContext smartOnFhirContext;

  public CurrentContextDto getCurrentContext() {
    return new CurrentContextDto(getPatient(), getUser());
  }

  protected PatientDto getPatient() {
    String patientId = smartOnFhirContext.getPatient();
    Assert.notNull(patientId, "Patient id cannot be null.");
    Patient patient = ehrClient.read()
        .resource(Patient.class)
        .withId(patientId)
        .execute();
    return new PatientToDtoConverter().convert(patient);
  }

  protected UserDto getUser() {
    String userProfile = smartOnFhirContext.getProfile();
    Assert.notNull(userProfile, "Current User cannot be null.");
    // TODO support other user Resource types. For example another Patient can create Tasks.
    Assert.isTrue(Practitioner.class.getSimpleName()
            .equals(StringUtils.substringBefore(userProfile, "/")),
        "Current user is not a Practitioner. Only Practitioner resource type is supported for now.");
    Practitioner practitioner = ehrClient.read()
        .resource(Practitioner.class)
        .withId(StringUtils.substringAfter(userProfile, "/"))
        .execute();
    return new PractitionerToDtoConverter().convert(practitioner);
  }
}