package org.hl7.gravity.refimpl.sdohexchange.fhir.reference;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reference resolver for {@link Task} resource.
 */
public class TaskReferenceResolver implements ReferenceResolver {

  private final Task task;
  private final String identifierSystem;

  private Patient localPatient;
  private Organization localRequester;

  private Patient externalPatient;
  private Organization externalRequester;
  private ServiceRequest externalServiceRequest;

  public TaskReferenceResolver(Task task, String identifierSystem) {
    this.task = task;
    this.identifierSystem = identifierSystem;
  }

  @Override
  public List<Reference> getLocalReferences() {
    return Arrays.asList(task.getFor(), task.getRequester());
  }

  @Override
  public void setLocalResources(Map<Class<? extends Resource>, List<Resource>> localResources) {
    this.localPatient = getFirstResource(localResources, Patient.class);
    this.localRequester = getFirstResource(localResources, Organization.class);
  }

  @Override
  public void setExternalResources(Map<Class<? extends Resource>, List<Resource>> externalResources) {
    this.externalPatient = getFirstResource(externalResources, Patient.class);
    this.externalRequester = getFirstResource(externalResources, Organization.class);
    this.externalServiceRequest = getFirstResource(externalResources, ServiceRequest.class);
  }

  @Override
  public List<Reference> getExternalReferences() {
    List<Reference> externalReferences = new ArrayList<>();
    if (localPatient == null) {
      externalReferences.add(task.getFor());
    }
    if (localRequester == null) {
      externalReferences.add(task.getRequester());
    }
    externalReferences.add(task.getFocus());
    return externalReferences;
  }

  public boolean createPatient() {
    return localPatient == null;
  }

  public boolean createRequester() {
    return localRequester == null;
  }

  public Patient getPatient() {
    if (localPatient == null) {
      Patient patient = externalPatient.copy();
      patient.addIdentifier()
          .setSystem(identifierSystem)
          .setValue(patient.getIdElement()
              .getIdPart());
      patient.setId(IdType.newRandomUuid());
      return patient;
    }
    return localPatient;
  }

  public Organization getRequester() {
    if (localRequester == null) {
      Organization requester = externalRequester.copy();
      // Remove SDOH profile, Logica does not support this.
      // TODO Use SDOH Profiles.
      requester.setMeta(null);
      requester.addIdentifier()
          .setSystem(identifierSystem)
          .setValue(requester.getIdElement()
              .getIdPart());
      requester.setId(IdType.newRandomUuid());
      return requester;
    }
    return localRequester;
  }

  public ServiceRequest getServiceRequest() {
    ServiceRequest serviceRequest = externalServiceRequest.copy();
    // Remove SDOH profile, Logica does not support this.
    // TODO Use SDOH Profiles.
    serviceRequest.setMeta(null);
    serviceRequest.addIdentifier()
        .setSystem(identifierSystem)
        .setValue(serviceRequest.getIdElement()
            .getIdPart());
    serviceRequest.setId(IdType.newRandomUuid());
    return serviceRequest;
  }

  private <T extends Resource> T getFirstResource(Map<Class<? extends Resource>, List<Resource>> resources,
      Class<T> key) {
    return Optional.ofNullable(resources.get(key))
        .orElse(Collections.emptyList())
        .stream()
        .filter(key::isInstance)
        .map(key::cast)
        .findFirst()
        .orElse(null);
  }
}
