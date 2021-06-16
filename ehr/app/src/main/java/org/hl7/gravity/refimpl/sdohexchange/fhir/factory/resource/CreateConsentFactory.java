package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.resource;

import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.codesystems.ConsentAction;
import org.hl7.fhir.r4.model.codesystems.ConsentPolicy;
import org.hl7.fhir.r4.model.codesystems.ConsentScope;
import org.hl7.fhir.r4.model.codesystems.V3ActCode;
import org.hl7.fhir.r4.model.codesystems.V3RoleClass;
import org.hl7.gravity.refimpl.sdohexchange.exception.ConsentCreateException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Mykhailo Stefantsiv
 */
@AllArgsConstructor
public class CreateConsentFactory {

  private final String name;
  private final Reference patient;
  private final MultipartFile attachment;
  private final Reference organization;

  public Consent createConsent() {
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
    consent.setPatient(patient);

    ConsentPolicy consentPolicy = ConsentPolicy.HIPAAAUTH;
    consent.getPolicyRule()
        .addCoding(new Coding(consentPolicy.getSystem(), consentPolicy.toCode(), consentPolicy.getDisplay()));
    V3RoleClass roleClass = V3RoleClass.PAT;
    consent.getProvision().addActor().setReference(patient)
        .getRole()
        .addCoding(new Coding(roleClass.getSystem(), roleClass.toCode(), roleClass.getDisplay()));

    ConsentAction consentAction = ConsentAction.DISCLOSE;
    consent.getProvision().addAction().getCoding()
        .add(new Coding(consentAction.getSystem(), consentAction.toCode(), consentAction.getDisplay()));
    consent.getOrganization().add(organization);

    return consent;
  }


}
