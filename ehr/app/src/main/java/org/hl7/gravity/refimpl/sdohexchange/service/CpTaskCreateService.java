package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for CP interaction. This logic definitely should not have been a service class, but, for simplicity,
 * implemented like this.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CpTaskCreateService {

  private final FhirContext fhirContext;
  @Value("${ehr.open-fhir-server-uri}")
  private String identifierSystem;

  public void createTask(Task ehrTask, Endpoint endpoint) throws CpTaskCreateException {
    IGenericClient cpClient = fhirContext.newRestfulGenericClient(endpoint.getAddress());

    Task cpTask = externalizeResource(ehrTask.copy());
    //TODO: Remove this after Logica bug response
    cpTask.setMeta(null);
    cpTask.addIdentifier()
        .setSystem(identifierSystem)
        .setValue(ehrTask.getIdElement()
            .getIdPart());
    try {
      cpClient.create()
          .resource(cpTask)
          .execute();
    } catch (BaseServerResponseException exc) {
      throw new CpTaskCreateException(
          String.format("Could not create a Task with identifier '%s' in CP at '%s'. Reason: %s.",
              identifierSystem + "|" + ehrTask.getIdElement()
                  .getIdPart(), cpClient.getServerBase(), exc.getMessage()), exc);
    }
  }

  protected <T extends Resource> T externalizeResource(T resource) {
    for (Reference reference : FhirUtil.getAllReferences(fhirContext, resource)) {
      IIdType el = reference.getReferenceElement();
      el.setParts(identifierSystem, el.getResourceType(), el.getIdPart(), null);
      reference.setReferenceElement(el);
    }
    resource.setId(IdType.newRandomUuid());
    return resource;
  }

  /**
   * Exception that should be thrown when task creation process failed in CBRO. This will usually lead to a Task with a
   * status FAILED and corresponding statusReason.
   */
  public static class CpTaskCreateException extends Exception {

    public CpTaskCreateException(String message) {
      super(message);
    }

    public CpTaskCreateException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}