package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import com.google.common.base.Strings;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.EndpointConnectionType;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TasksPollingBundleExtractor.TasksPollingInfo;

public class TasksPollingBundleExtractor extends BundleExtractor<TasksPollingInfo> {

  @Override
  public TasksPollingInfo extract(Bundle bundle) {
    Map<? extends Class<? extends Resource>, Map<String, Resource>> taskResources = bundle.getEntry()
        .stream()
        .map(BundleEntryComponent::getResource)
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(Resource::getClass, Collectors.toMap(r -> r.getIdElement()
            .getIdPart(), Function.identity())));
    return new TasksPollingInfo(taskResources);
  }

  public class TasksPollingInfo {

    private final Map<String, Task> tasks;
    private final Map<String, ServiceRequest> serviceRequests;
    private final Map<String, Organization> organizations;
    private final Map<String, Endpoint> endpoints;

    public TasksPollingInfo(Map<? extends Class<? extends Resource>, Map<String, Resource>> taskResources) {
      this.tasks = collectResources(taskResources, Task.class);
      this.serviceRequests = collectResources(taskResources, ServiceRequest.class);
      this.organizations = collectResources(taskResources, Organization.class);
      this.endpoints = collectResources(taskResources, Endpoint.class);
    }

    public Collection<Task> getTasks() {
      return tasks.values();
    }

    public ServiceRequest getServiceRequest(Task task) {
      return serviceRequests.get(task.getFocus()
          .getReferenceElement()
          .getIdPart());
    }

    public Organization getOrganization(Task task) {
      return organizations.get(task.getOwner()
          .getReferenceElement()
          .getIdPart());
    }

    public Endpoint getEndpoint(Organization organization) throws TaskPollingUpdateException {
      if (organization.getEndpoint()
          .size() != 1) {
        throw new TaskPollingUpdateException("Task owner Organization has 0 or more that 1 endpoints.");
      } else {
        Endpoint endpoint = endpoints.get(organization.getEndpoint()
            .get(0)
            .getReferenceElement()
            .getIdPart());
        if (endpoint == null) {
          String reason = String.format("Organization resource with id '%s' does not contain endpoint of type '%s'.",
              organization.getIdElement()
                  .getIdPart(), EndpointConnectionType.HL7FHIRREST);
          throw new TaskPollingUpdateException(reason);
        } else {
          if (Strings.isNullOrEmpty(endpoint.getAddress())) {
            String reason = String.format("Endpoint resource with id '%s' does not contain an address.",
                endpoint.getIdElement()
                    .getIdPart());
            throw new TaskPollingUpdateException(reason);
          }
        }
        return endpoint;
      }
    }

    private <R extends Resource> Map<String, R> collectResources(
        Map<? extends Class<? extends Resource>, Map<String, Resource>> resources,
        Class<R> resourceClass) {
      return Optional.ofNullable(resources.get(resourceClass))
          .orElse(Collections.emptyMap())
          .entrySet()
          .stream()
          .collect(Collectors.toMap(Entry::getKey, entry -> resourceClass.cast(entry.getValue())));
    }
  }

  /**
   * Exception that should be thrown when task update process failed. This will usually lead to a Task with a status
   * FAILED and corresponding statusReason.
   */
  public static class TaskPollingUpdateException extends Exception {

    public TaskPollingUpdateException(String message) {
      super(message);
    }
  }
}