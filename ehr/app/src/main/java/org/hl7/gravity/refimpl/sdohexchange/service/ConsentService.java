package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.ConsentRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.ConsentToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.BaseConsentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConsentAttachmentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConsentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.ConsentCreateException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.ConsentPrepareBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.resource.CreateConsentFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Mykhailo Stefantsiv
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ConsentService {

  private final SmartOnFhirContext smartOnFhirContext;
  private final ConsentRepository consentRepository;
  private final IGenericClient ehrClient;

  public List<ConsentDto> listConsents() {
    Bundle bundle = consentRepository.findAllByPatient(smartOnFhirContext.getPatient());
    List<Consent> consentResources = FhirUtil.getFromBundle(bundle, Consent.class);
    return consentResources.stream()
        .map(consent -> new ConsentToDtoConverter().convert(consent))
        .collect(Collectors.toList());
  }

  public List<BaseConsentDto> listBaseConsentsInfo() {
    Bundle bundle = consentRepository.findAllByPatient(smartOnFhirContext.getPatient());
    List<Consent> consentResources = FhirUtil.getFromBundle(bundle, Consent.class);
    return consentResources.stream()
        .map(consent ->
            new BaseConsentDto(consent.getIdElement().getIdPart(), consent.getSourceAttachment().getTitle()))
        .collect(Collectors.toList());
  }

  public ConsentDto createConsent(String name, MultipartFile attachment, UserDto userDto) {
    Reference patient = FhirUtil.toReference(Patient.class, smartOnFhirContext.getPatient());
    Reference organization = retrieveOrganization(userDto);
    Consent consent = new CreateConsentFactory(name, patient, attachment, organization).createConsent();

    MethodOutcome methodOutcome = ehrClient.create().resource(consent).execute();
    Consent savedConsent = (Consent) methodOutcome.getResource();
    return new ConsentToDtoConverter().convert(savedConsent);
  }

  public ConsentAttachmentDto retrieveAttachmentInfo(String id) {
    Optional<Consent> foundConsent = consentRepository.find(id);
    if (!foundConsent.isPresent()) {
      throw new ResourceNotFoundException(String.format("Consent with id '%s' was not found.", id));
    }
    Consent consent = foundConsent.get();
    Attachment attachment = consent.getSourceAttachment();
    return ConsentAttachmentDto.builder()
        .content(attachment.getData())
        .contentType(attachment.getContentType())
        .title(attachment.getTitle())
        .build();
  }

  private Reference retrieveOrganization(UserDto userDto) {
    Bundle bundle = new ConsentPrepareBundleFactory(userDto.getId()).createPrepareBundle();
    Bundle consentResponseBundle = ehrClient.transaction()
        .withBundle(bundle)
        .execute();
    Bundle consentBundle = FhirUtil.getFirstFromBundle(consentResponseBundle, Bundle.class);
    PractitionerRole practitionerRole = FhirUtil.getFirstFromBundle(consentBundle, PractitionerRole.class);
    Reference organization = practitionerRole.getOrganization();
    if (organization == null) {
      throw new ConsentCreateException("No Organization found for Consent creation.");
    }
    return organization;
  }

}
