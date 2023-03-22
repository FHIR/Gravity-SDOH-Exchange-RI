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
  // TODO: STU2: Org does not have a required endpoit. this is a temporary
  // solution.
  // TODO: Need to know how to extract the cp base url. or we can have cp pull
  // task from ehr.
  private String cpBaseUrl = "http://localhost:8080/fhir";

  public void create(final Task ehrTask) throws CpClientException {
    Task cpTask = externalizeResource(ehrTask.copy());
    // TODO: Remove this after Logica bug response
    cpTask.setMeta(null);
    cpTask.addIdentifier()
        .setSystem(identifierSystem)
        .setValue(ehrTask.getIdElement()
            .getIdPart());
    try {
      cpClient().create()
          .resource(cpTask)
          .execute();
    } catch (BaseServerResponseException exc) {
      throw new CpClientException(
          String.format("Could not create a Task with identifier '%s' in CP at '%s'. Reason: %s.",
              identifierSystem + "|" + ehrTask.getIdElement()
                  .getIdPart(),
              cpBaseUrl, exc.getMessage()),
          exc);
    }
  }

  public TaskInfoHolder read(final String id) throws CpClientException {
    try {
      Bundle taskBundle = cpClient().search()
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
            cpBaseUrl, identifierSystem + "|" + id));
      } else if (tasksSize > 1) {
        throw new CpClientException(String.format("More than one Task is present at '%s' for identifier '%s'.",
            cpBaseUrl, identifierSystem + "|" + id));
      }
      return new TaskInfoBundleExtractor().extract(taskBundle)
          .stream()
          .findFirst()
          .get();
    } catch (BaseServerResponseException exc) {
      throw new CpClientException(
          String.format("Task retrieval failed for identifier '%s' at CP location '%s'. Reason: %s.",
              identifierSystem + "|" + id, cpBaseUrl, exc.getMessage()),
          exc);
    }
  }

  public void sync(final Task ehrTask, final ServiceRequest ehrServiceRequest)
      throws CpClientException {
    Bundle cpUpdateBundle = new Bundle();
    cpUpdateBundle.setType(BundleType.TRANSACTION);

    TaskInfoHolder cpTaskInfo = read(ehrTask.getIdElement()
        .getIdPart());
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
    transaction(cpUpdateBundle);
  }

  public Bundle search(final List<String> ids, Class<? extends IBaseResource> resource) {
    return cpClient().search()
        .forResource(resource)
        .where(Resource.RES_ID.exactly()
            .codes(ids))
        .returnBundle(Bundle.class)
        .execute();
  }

  protected Bundle transaction(final Bundle transactionBundle) throws CpClientException {
    try {
      return cpClient().transaction()
          .withBundle(transactionBundle)
          .execute();
    } catch (BaseServerResponseException exc) {
      throw new CpClientException(
          String.format("Transaction execution failed at CP location '%s'. Reason: %s.", cpBaseUrl,
              exc.getMessage()),
          exc);
    }
  }

  protected <T extends Resource> T externalizeResource(T resource) {
    FhirUtil.getAllReferences(fhirContext, resource)
        .forEach(reference -> externalizeReference(reference));
    resource.setId(IdType.newRandomUuid());
    return resource;
  }

  protected void externalizeReference(Reference reference) {
    IIdType el = reference.getReferenceElement();
    el.setParts(cpBaseUrl, el.getResourceType(), el.getIdPart(), null);
    reference.setReferenceElement(el);
  }

  private IGenericClient cpClient() {
    return fhirContext.newRestfulGenericClient(cpBaseUrl);
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
