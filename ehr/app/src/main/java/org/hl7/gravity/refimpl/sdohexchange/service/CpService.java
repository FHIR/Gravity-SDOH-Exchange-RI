package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.info.ServiceRequestInfo;
import org.hl7.gravity.refimpl.sdohexchange.info.TaskInfo;
import org.hl7.gravity.refimpl.sdohexchange.info.composer.TasksInfoComposer;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CpService {

  private final FhirContext fhirContext;
  private final TasksInfoComposer tasksInfoComposer;
  @Value("${ehr.open-fhir-server-uri}")
  private String identifierSystem;

  public TaskInfo getTask(String id, Endpoint endpoint) throws CpClientException {
    IGenericClient cpClient = fhirContext.newRestfulGenericClient(endpoint.getAddress());
    Bundle cpTaskBundle = null;
    try {
      cpTaskBundle = cpClient.search()
          .forResource(Task.class)
          .where(Task.IDENTIFIER.exactly()
              .systemAndValues(identifierSystem, id))
          .include(Task.INCLUDE_FOCUS)
          .returnBundle(Bundle.class)
          .execute();
    } catch (
        BaseServerResponseException exc) {
      throw new CpClientException(
          String.format("Task retrieval failed for identifier '%s' at CP location '%s'. Reason: %s.",
              identifierSystem + "|" + id, cpClient.getServerBase(), exc.getMessage()), exc);
    }
    List<Task> tasks = FhirUtil.getFromBundle(cpTaskBundle, Task.class);
    if (tasks.size() == 0) {
      throw new CpClientException(
          String.format("No Task is present at '%s' for identifier '%s'.", cpClient.getServerBase(),
              identifierSystem + "|" + id));
    } else if (tasks.size() > 1) {
      throw new CpClientException(
          String.format("More than one Task is present at '%s' for identifier '%s'.", cpClient.getServerBase(),
              identifierSystem + "|" + id));
    }
    List<ServiceRequest> serviceRequests = FhirUtil.getFromBundle(cpTaskBundle, ServiceRequest.class);
    return new TaskInfo(tasks.get(0), new ServiceRequestInfo(serviceRequests.get(0), null, null, null), null);
  }

  public Bundle transaction(Bundle bundle, Endpoint endpoint) throws CpClientException {
    IGenericClient cpClient = fhirContext.newRestfulGenericClient(endpoint.getAddress());
    try {
      return cpClient.transaction()
          .withBundle(bundle)
          .execute();
    } catch (BaseServerResponseException exc) {
      throw new CpClientException(
          String.format("Transaction execution failed at CP location '%s'. Reason: %s.", cpClient.getServerBase(),
              exc.getMessage()), exc);
    }
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
