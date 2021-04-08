package org.hl7.gravity.refimpl.sdohexchange.fhir.reference;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskReferenceResourcesLoader {

  private final FhirContext fhirContext;

  public Bundle getReferenceResources(Task task) {
    // Get identifier system name to link with EHR
    //TODO: Right now we support only single identifier, reimplement in future
    String identifierSystem = task.getIdentifier()
        .get(0)
        .getSystem();
    Reference taskFocus = task.getFocus();
    // Get EHR server base from ServiceRequest reference and create EHR Client
    //TODO: Right now we take url from Task reference, but we should take it from Organization resource and use secured
    // client
    IGenericClient ehrClient = fhirContext.newRestfulGenericClient(taskFocus.getReferenceElement()
        .getBaseUrl());
    // Load ServiceRequest from EHR system
    ServiceRequest serviceRequest = loadServiceRequest(ehrClient, taskFocus.getReferenceElement()
        .getIdPart(), identifierSystem);

    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);

    for (Reference serviceRequestRef : FhirUtil.getAllReferences(fhirContext, serviceRequest)) {
      IIdType serviceRequestEl = serviceRequestRef.getReferenceElement();
      if (serviceRequestEl.getResourceType()
          .equals(Consent.class.getSimpleName())) {
        // Load Consent from EHR system
        Consent consent = loadConsent(ehrClient, serviceRequestEl.getIdPart(), identifierSystem);
        // Update all Consent references to link with EHR
        for (Reference consentRef : FhirUtil.getAllReferences(fhirContext, consent)) {
          IIdType consentEl = consentRef.getReferenceElement();
          consentEl.setParts(ehrClient.getServerBase(), consentEl.getResourceType(), consentEl.getIdPart(), null);
          consentRef.setReferenceElement(consentEl);
        }
        // Create CBRO Consent resource
        bundle.addEntry(FhirUtil.createPostEntry(consent));
        // Link CBRO ServiceRequest with Consent
        serviceRequestEl.setParts(null, serviceRequestEl.getResourceType(), consent.getIdElement()
            .getValue(), null);
        serviceRequestRef.setReferenceElement(serviceRequestEl);
      } else {
        // Update all ServiceRequest references to link with EHR
        serviceRequestEl.setParts(ehrClient.getServerBase(), serviceRequestEl.getResourceType(),
            serviceRequestEl.getIdPart(), null);
        serviceRequestRef.setReferenceElement(serviceRequestEl);
      }
    }
    // Create CBRO ServiceRequest resource
    bundle.addEntry(FhirUtil.createPostEntry(serviceRequest));
    // Link CBRO Task with ServiceRequest
    task.setFocus(FhirUtil.toReference(ServiceRequest.class, serviceRequest.getIdElement()
        .getValue()));
    return bundle;
  }

  private ServiceRequest loadServiceRequest(IGenericClient ehrClient, String ehrServiceRequestId,
      String identifierSystem) {
    // Load ServiceRequest from EHR system
    ServiceRequest serviceRequest = ehrClient.read()
        .resource(ServiceRequest.class)
        .withId(ehrServiceRequestId)
        .execute()
        .copy();
    // Set identifier to link resource from EHR
    serviceRequest.addIdentifier()
        .setSystem(identifierSystem)
        .setValue(serviceRequest.getIdElement()
            .getIdPart());
    serviceRequest.setId(IdType.newRandomUuid());
    return serviceRequest;
  }

  private Consent loadConsent(IGenericClient ehrClient, String ehrConsentId, String identifierSystem) {
    // Load Consent from EHR system
    Consent consent = ehrClient.read()
        .resource(Consent.class)
        .withId(ehrConsentId)
        .execute()
        .copy();
    // Set identifier to link resource from EHR
    consent.addIdentifier()
        .setSystem(identifierSystem)
        .setValue(consent.getIdElement()
            .getIdPart());
    consent.setId(IdType.newRandomUuid());
    return consent;
  }
}
