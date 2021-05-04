package org.hl7.gravity.refimpl.sdohexchange.fhir.reference;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskReferenceResourcesLoader {

  private final FhirContext fhirContext;
  private final IGenericClient openCbroClient;

  public Bundle getReferenceResources(Task task) {
    // Get identifier system name to link with EHR
    String identifier = task.getIdentifierFirstRep()
        .getSystem();

    // Get EHR server base from ServiceRequest reference and create EHR Client
    IGenericClient ehrClient = fhirContext.newRestfulGenericClient(identifier);

    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);

    TaskReferencesHolder taskReferencesHolder = loadTaskReferences(ehrClient, task, identifier, bundle);
    processServiceRequestReferences(ehrClient, taskReferencesHolder, identifier, bundle);
    return bundle;
  }

  private TaskReferencesHolder loadTaskReferences(IGenericClient ehrClient, Task task, String identifierSystem,
      Bundle bundle) {
    TaskReferenceResolver referenceResolver = new TaskReferenceResolver(task, identifierSystem);
    // Load local references in one transaction
    referenceResolver.setLocalResources(loadLocalResources(referenceResolver.getLocalReferences(), identifierSystem));
    // Load EHR references in one transaction
    referenceResolver.setExternalResources(
        loadReferenceResources(ehrClient, referenceResolver.getExternalReferences()));

    Patient patient = referenceResolver.getPatient();
    if (referenceResolver.createPatient()) {
      // Create CBRO Patient resource
      bundle.addEntry(FhirUtil.createPostEntry(patient));
    }
    Organization requester = referenceResolver.getRequester();
    if (referenceResolver.createRequester()) {
      // Create CBRO Organization resource
      bundle.addEntry(FhirUtil.createPostEntry(requester));
    }
    ServiceRequest serviceRequest = referenceResolver.getServiceRequest();
    // Link CBRO ServiceRequest with Patient
    serviceRequest.setSubject(FhirUtil.toReference(Patient.class, patient.getIdElement()
        .getIdPart()));
    // Create CBRO ServiceRequest resource
    bundle.addEntry(FhirUtil.createPostEntry(serviceRequest));

    // Link CBRO Task with Patient, Organization and ServiceRequest
    task.setFor(FhirUtil.toReference(Patient.class, patient.getIdElement()
        .getIdPart(), patient.getNameFirstRep()
        .getNameAsSingleString()));
    task.setRequester(FhirUtil.toReference(Organization.class, requester.getIdElement()
        .getIdPart(), requester.getName()));
    task.setFocus(FhirUtil.toReference(ServiceRequest.class, serviceRequest.getIdElement()
        .getIdPart()));
    return new TaskReferencesHolder(patient, requester, serviceRequest);
  }

  private void processServiceRequestReferences(IGenericClient ehrClient, TaskReferencesHolder taskReferencesHolder,
      String identifierSystem, Bundle bundle) {
    ServiceRequestReferenceResolver referenceResolver = new ServiceRequestReferenceResolver(fhirContext,
        taskReferencesHolder.getServiceRequest(), identifierSystem);
    // Load local references in one transaction
    referenceResolver.setLocalResources(loadLocalResources(referenceResolver.getLocalReferences(), identifierSystem));
    // Load EHR references in one transaction
    referenceResolver.setExternalResources(
        loadReferenceResources(ehrClient, referenceResolver.getExternalReferences()));

    Map<String, Consumer<IIdType>> referenceConsumers = new HashMap<>();
    referenceConsumers.put(Patient.class.getSimpleName(), new PatientReferenceConsumer(taskReferencesHolder.getPatient()
        .getIdElement()
        .getIdPart()));
    for (Reference serviceRequestRef : referenceResolver.getConsentsRef()) {
      IIdType serviceRequestEl = serviceRequestRef.getReferenceElement();
      Consent consent = referenceResolver.getConsent(serviceRequestEl);
      updateResourceRefs(consent, ehrClient.getServerBase(), referenceConsumers);
      // Create CBRO Goal resource
      bundle.addEntry(FhirUtil.createPostEntry(consent));
      // Link CBRO ServiceRequest reference with Gaol
      serviceRequestEl.setParts(null, serviceRequestEl.getResourceType(), consent.getIdElement()
          .getIdPart(), null);
      serviceRequestRef.setReferenceElement(serviceRequestEl);
    }
    Map<String, Condition> processedConditions = new HashMap<>();
    for (Reference serviceRequestRef : referenceResolver.getConditionsRefs()) {
      IIdType serviceRequestEl = serviceRequestRef.getReferenceElement();
      Condition condition = referenceResolver.getCondition(serviceRequestEl);
      // Update links only for new conditions
      if (referenceResolver.createCondition(serviceRequestEl)) {
        updateResourceRefs(condition, ehrClient.getServerBase(), referenceConsumers);
        // Create CBRO Condition resource
        bundle.addEntry(FhirUtil.createPostEntry(condition));
      }
      // Link CBRO ServiceRequest reference with Condition
      serviceRequestEl.setParts(null, serviceRequestEl.getResourceType(), condition.getIdElement()
          .getIdPart(), null);
      serviceRequestRef.setReferenceElement(serviceRequestEl);
      processedConditions.put(condition.getIdentifierFirstRep()
          .getValue(), condition);
    }
    referenceConsumers.put(Condition.class.getSimpleName(), new ConditionReferenceConsumer(processedConditions));

    for (Reference serviceRequestRef : referenceResolver.getGoalsRefs()) {
      IIdType serviceRequestEl = serviceRequestRef.getReferenceElement();
      Goal goal = referenceResolver.getGoal(serviceRequestEl);
      if (referenceResolver.createGoal(serviceRequestEl)) {
        updateResourceRefs(goal, ehrClient.getServerBase(), referenceConsumers);
        // Create CBRO Goal resource
        bundle.addEntry(FhirUtil.createPostEntry(goal));
      }
      // Link CBRO ServiceRequest reference with Gaol
      serviceRequestEl.setParts(null, serviceRequestEl.getResourceType(), goal.getIdElement()
          .getIdPart(), null);
      serviceRequestRef.setReferenceElement(serviceRequestEl);
    }
  }

  private void updateResourceRefs(Resource resource, String serverBase,
      Map<String, Consumer<IIdType>> referenceConsumers) {
    for (Reference resourceRef : FhirUtil.getAllReferences(fhirContext, resource)) {
      IIdType resourceEl = resourceRef.getReferenceElement();
      Consumer<IIdType> referenceConsumer = referenceConsumers.get(resourceEl.getResourceType());
      if (referenceConsumer != null) {
        referenceConsumer.accept(resourceEl);
      } else {
        resourceEl.setParts(serverBase, resourceEl.getResourceType(), resourceEl.getIdPart(), null);
      }
      resourceRef.setReferenceElement(resourceEl);
    }
  }

  private Map<Class<? extends Resource>, List<Resource>> loadReferenceResources(IGenericClient ehrClient,
      List<Reference> references) {
    if (references.isEmpty()) {
      return Collections.emptyMap();
    }
    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);
    for (Reference reference : references) {
      IIdType idType = reference.getReferenceElement();
      bundle.addEntry()
          .getRequest()
          .setMethod(Bundle.HTTPVerb.GET)
          .setUrl(idType.getResourceType() + "/" + idType.getIdPart());
    }
    return ehrClient.transaction()
        .withBundle(bundle)
        .execute()
        .getEntry()
        .stream()
        .map(Bundle.BundleEntryComponent::getResource)
        .collect(Collectors.groupingBy(Resource::getClass));
  }

  private Map<Class<? extends Resource>, List<Resource>> loadLocalResources(List<Reference> references, String system) {
    if (references.isEmpty()) {
      return Collections.emptyMap();
    }
    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);
    for (Reference reference : references) {
      IIdType idType = reference.getReferenceElement();
      bundle.addEntry()
          .getRequest()
          .setMethod(Bundle.HTTPVerb.GET)
          .setUrl(idType.getResourceType() + "?identifier=" + system + "|" + idType.getIdPart());
    }
    return openCbroClient.transaction()
        .withBundle(bundle)
        .execute()
        .getEntry()
        .stream()
        .map(Bundle.BundleEntryComponent::getResource)
        .filter(Bundle.class::isInstance)
        .map(Bundle.class::cast)
        .map(Bundle::getEntry)
        .flatMap(List::stream)
        .map(Bundle.BundleEntryComponent::getResource)
        .collect(Collectors.groupingBy(Resource::getClass));
  }

  @AllArgsConstructor
  private static class PatientReferenceConsumer implements Consumer<IIdType> {

    private final String patientId;

    @Override
    public void accept(IIdType resourceEl) {
      resourceEl.setParts(null, resourceEl.getResourceType(), patientId, null);
    }
  }

  @AllArgsConstructor
  private static class ConditionReferenceConsumer implements Consumer<IIdType> {

    private final Map<String, Condition> processedConditions;

    @Override
    public void accept(IIdType resourceEl) {
      Condition condition = processedConditions.get(resourceEl.getIdPart());
      resourceEl.setParts(null, resourceEl.getResourceType(), condition.getIdElement()
          .getIdPart(), null);
    }
  }
}