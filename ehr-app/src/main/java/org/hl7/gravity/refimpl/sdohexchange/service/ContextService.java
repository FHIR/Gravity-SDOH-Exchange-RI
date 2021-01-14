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
import org.hl7.gravity.refimpl.sdohexchange.fhir.IGenericClientProvider;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SmartOnFhirContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ContextService {

  private final IGenericClientProvider clientProvider;
  private final SmartOnFhirContextProvider smartProvider;

  public CurrentContextDto getCurrentContext() {
    SmartOnFhirContext smartOnFhirContext = smartProvider.context();
    return new CurrentContextDto(getPatient(smartOnFhirContext), getUser(smartOnFhirContext));
  }

  protected PatientDto getPatient(SmartOnFhirContext smartOnFhirContext) {
    String patientId = smartOnFhirContext.getPatient();
    Assert.notNull(patientId, "Patient id cannot be null.");
    IGenericClient client = clientProvider.client();
    Patient patient = client.read()
        .resource(Patient.class)
        .withId(patientId)
        .execute();
    return new PatientToDtoConverter().convert(patient);
  }

  protected UserDto getUser(SmartOnFhirContext smartOnFhirContext) {
    String userProfile = smartOnFhirContext.getProfile();
    // Logica Sandbox has issues with logging in with a Persona (https://logica.atlassian.net/browse/SNDBX-1486).
    // This is why, when launching as a standalone app, there is no Fhir Resource associated with a current user. We
    // will just set everything we can.
    if (userProfile == null) {
      return getUserFromAuthentication();
    }
    Assert.notNull(userProfile, "Current User cannot be null.");
    // TODO support other user Resource types. For example another Patient can create Tasks.
    Assert.isTrue(Practitioner.class.getSimpleName()
            .equals(StringUtils.substringBefore(userProfile, "/")),
        "Current user is not a Practitioner. Only Practitioner resource type is supported for now.");
    IGenericClient client = clientProvider.client();
    Practitioner practitioner = client.read()
        .resource(Practitioner.class)
        .withId(StringUtils.substringAfter(userProfile, "/"))
        .execute();
    return new PractitionerToDtoConverter().convert(practitioner);

  }

  private UserDto getUserFromAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();
    Assert.notNull(authentication, "Authentication object is missing from security context.");
    UserDto userDto = new UserDto(null, Practitioner.class.getSimpleName());
    userDto.setName(((OAuth2User) authentication.getPrincipal()).getAttribute("displayName"));
    return userDto;
  }
}