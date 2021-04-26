package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for CBRO interaction. This logic definitely should not have been a service class, but, for simplicity,
 * implemented like this.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CbroTaskCreateService {

  private final FhirContext context;
  @Value("${ehr.identifier-system}")
  private String identifierSystem;
  @Value("${ehr.open-fhir-server-uri}")
  private String openFhirServerUri;

  public void createTask(IGenericClient cbroClient, Task task) throws CbroTaskCreateException {
    Task t = externalizeResource(task.copy());
    t.addIdentifier()
        .setSystem(identifierSystem)
        .setValue(task.getIdElement()
            .getIdPart());
    t.setStatus(Task.TaskStatus.REQUESTED);
    try {
      cbroClient.create()
          .resource(t)
          .execute();
    } catch (BaseServerResponseException exc) {
      throw new CbroTaskCreateException(
          String.format("Could not create a Task with identifier '%s' in CBRO at '%s'. Reason: %s.",
              identifierSystem + "|" + task.getIdElement()
                  .getIdPart(), cbroClient.getServerBase(), exc.getMessage()), exc);
    }
  }

  protected <T extends Resource> T externalizeResource(T resource) {
    for (Reference reference : FhirUtil.getAllReferences(context, resource)) {
      IIdType el = reference.getReferenceElement();
      el.setParts(openFhirServerUri, el.getResourceType(), el.getIdPart(), null);
      reference.setReferenceElement(el);
    }
    resource.setId(IdType.newRandomUuid());
    return resource;
  }

  /**
   * Exception that should be thrown when task creation process failed in CBRO. This will usually lead to a Task with a
   * status FAILED and corresponding statusReason.
   */
  public static class CbroTaskCreateException extends Exception {

    public CbroTaskCreateException(String message) {
      super(message);
    }

    public CbroTaskCreateException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}