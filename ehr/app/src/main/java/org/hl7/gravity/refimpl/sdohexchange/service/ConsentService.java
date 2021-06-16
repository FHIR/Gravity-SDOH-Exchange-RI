package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.codesystems.ConsentAction;
import org.hl7.fhir.r4.model.codesystems.ConsentPolicy;
import org.hl7.fhir.r4.model.codesystems.ConsentScope;
import org.hl7.fhir.r4.model.codesystems.V3ActCode;
import org.hl7.fhir.r4.model.codesystems.V3RoleClass;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.ConsentRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.BaseConsentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConsentAttachmentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConsentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.ConsentCreateException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.ConsentPrepareBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        .map(consent -> ConsentDto.builder()
            .id(consent.getIdElement().getIdPart())
            .name(consent.getSourceAttachment().getTitle())
            .attachment(consent.getSourceAttachment().getData())
            .scope(consent.getScope().getCodingFirstRep().getDisplay())
            .status(consent.getStatus().getDisplay())
            .organization(consent.getOrganization().get(0).getDisplay())
            .build())
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
    Consent consent = new Consent();

    consent.getSourceAttachment().setTitle(name);
    try {
      consent.getSourceAttachment().setData(attachment.getBytes());
    } catch (IOException e) {
      throw new ConsentCreateException("Consent attachment cannot be read.");
    }
    consent.getSourceAttachment().setContentType(MediaType.APPLICATION_PDF_VALUE);

    consent.setId(IdType.newRandomUuid());
    consent.setStatus(Consent.ConsentState.ACTIVE);
    consent.setDateTimeElement(DateTimeType.now());
    consent.getMeta().addProfile(SDOHProfiles.CONSENT);

    ConsentScope consentScope = ConsentScope.PATIENTPRIVACY;
    consent.getScope()
        .addCoding(new Coding(consentScope.getSystem(), consentScope.toCode(), consentScope.getDisplay()));
    V3ActCode actCode = V3ActCode.IDSCL;
    consent.addCategory(
        new CodeableConcept().addCoding(new Coding(actCode.getSystem(), actCode.toCode(), actCode.getDisplay())));
    consent.setPatient(FhirUtil.toReference(Patient.class, smartOnFhirContext.getPatient()));

    ConsentPolicy consentPolicy = ConsentPolicy.HIPAAAUTH;
    consent.getPolicyRule()
        .addCoding(new Coding(consentPolicy.getSystem(), consentPolicy.toCode(), consentPolicy.getDisplay()));
    V3RoleClass roleClass = V3RoleClass.PAT;
    consent.getProvision().addActor().setReference(FhirUtil.toReference(Patient.class, smartOnFhirContext.getPatient()))
        .getRole()
        .addCoding(new Coding(roleClass.getSystem(), roleClass.toCode(), roleClass.getDisplay()));

    ConsentAction consentAction = ConsentAction.DISCLOSE;
    consent.getProvision().addAction().getCoding()
        .add(new Coding(consentAction.getSystem(), consentAction.toCode(), consentAction.getDisplay()));
    consent.getOrganization().add(retrieveOrganization(userDto));

    MethodOutcome outcome = ehrClient.create().resource(consent).execute();
    Consent savedConsent = (Consent) outcome.getResource();
    return ConsentDto.builder()
        .id(savedConsent.getIdElement().getIdPart())
        .name(savedConsent.getSourceAttachment().getTitle())
        .attachment(savedConsent.getSourceAttachment().getData())
        .scope(savedConsent.getScope().getCodingFirstRep().getDisplay())
        .status(savedConsent.getStatus().getDisplay())
        .organization(savedConsent.getOrganization().get(0).getDisplay())
        .build();
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
