package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor.TaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CpService {

  private final FhirContext fhirContext;
  @Value("${ehr.open-fhir-server-uri}")
  private String identifierSystem;

  public void create(final Task ehrTask, final Endpoint endpoint) throws CpClientException {
    Task cpTask = externalizeResource(ehrTask.copy(), identifierSystem);
    //TODO: Remove this after Logica bug response
    cpTask.setMeta(null);
    cpTask.addIdentifier()
        .setSystem(identifierSystem)
        .setValue(ehrTask.getIdElement()
            .getIdPart());
    try {
      cpClient(endpoint).create()
          .resource(cpTask)
          .execute();
    } catch (BaseServerResponseException exc) {
      throw new CpClientException(
          String.format("Could not create a Task with identifier '%s' in CP at '%s'. Reason: %s.",
              identifierSystem + "|" + ehrTask.getIdElement()
                  .getIdPart(), endpoint.getAddress(), exc.getMessage()), exc);
    }
  }

  public TaskInfoHolder read(final String id, final Endpoint endpoint) throws CpClientException {
    try {
      Bundle taskBundle = cpClient(endpoint).search()
          .forResource(Task.class)
          .where(Task.IDENTIFIER.exactly()
              .systemAndValues(identifierSystem, id))
          .include(Task.INCLUDE_FOCUS)
          .returnBundle(Bundle.class)
          .execute();
      // Additional validation
      int tasksSize = FhirUtil.getFromBundle(taskBundle, Task.class)
          .size();
      if (tasksSize == 0) {
        throw new CpClientException(String.format("No Task is present at '%s' for identifier '%s'.",
            endpoint.getAddress(), identifierSystem + "|" + id));
      } else if (tasksSize > 1) {
        throw new CpClientException(String.format("More than one Task is present at '%s' for identifier '%s'.",
            endpoint.getAddress(), identifierSystem + "|" + id));
      }
      return new TaskInfoBundleExtractor().extract(taskBundle)
          .stream()
          .findFirst()
          .get();
    } catch (BaseServerResponseException exc) {
      throw new CpClientException(
          String.format("Task retrieval failed for identifier '%s' at CP location '%s'. Reason: %s.",
              identifierSystem + "|" + id, endpoint.getAddress(), exc.getMessage()), exc);
    }
  }

  public void sync(final Task ehrTask, final ServiceRequest ehrServiceRequest, final Endpoint endpoint)
      throws CpClientException {
    Bundle cpUpdateBundle = new Bundle();
    cpUpdateBundle.setType(BundleType.TRANSACTION);

    TaskInfoHolder cpTaskInfo = read(ehrTask.getIdElement()
        .getIdPart(), endpoint);
    Task cpTask = cpTaskInfo.getTask();
    cpTask.setStatus(ehrTask.getStatus());
    cpTask.setStatusReason(ehrTask.getStatusReason());
    cpTask.setLastModifiedElement(ehrTask.getLastModifiedElement());
    cpTask.setNote(ehrTask.getNote());
    cpUpdateBundle.addEntry(FhirUtil.createPutEntry(cpTask));

    ServiceRequest cpServiceRequest = cpTaskInfo.getServiceRequest();
    if (!ehrServiceRequest.getStatus()
        .equals(cpServiceRequest.getStatus())) {
      cpServiceRequest.setStatus(ehrServiceRequest.getStatus());
      cpUpdateBundle.addEntry(FhirUtil.createPutEntry(cpServiceRequest));
    }
    transaction(cpUpdateBundle, endpoint);
  }

  public Bundle search(final List<String> ids, Class<? extends IBaseResource> resource, Endpoint endpoint) {
    return cpClient(endpoint).search()
        .forResource(resource)
        .where(Resource.RES_ID.exactly()
            .codes(ids))
        .returnBundle(Bundle.class)
        .execute();
  }

  protected Bundle transaction(final Bundle transactionBundle, final Endpoint endpoint) throws CpClientException {
    try {
      return cpClient(endpoint).transaction()
          .withBundle(transactionBundle)
          .execute();
    } catch (BaseServerResponseException exc) {
      throw new CpClientException(
          String.format("Transaction execution failed at CP location '%s'. Reason: %s.", endpoint.getAddress(),
              exc.getMessage()), exc);
    }
  }

  protected <T extends Resource> T externalizeResource(T resource, String baseUrl) {
    FhirUtil.getAllReferences(fhirContext, resource)
        .forEach(reference -> externalizeReference(reference, baseUrl));
    resource.setId(IdType.newRandomUuid());
    return resource;
  }

  protected void externalizeReference(Reference reference, String baseUrl) {
    IIdType el = reference.getReferenceElement();
    el.setParts(baseUrl, el.getResourceType(), el.getIdPart(), null);
    reference.setReferenceElement(el);
  }

  private IGenericClient cpClient(Endpoint endpoint) {
    return fhirContext.newRestfulGenericClient(endpoint.getAddress());
  }

  /**
   * Exception that should be thrown when CP client request failed.
   */
  public static class CpClientException extends Exception {

    public CpClientException(String message) {
      super(message);
    }

    public CpClientException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}