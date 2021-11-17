package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.EndpointConnectionType;
import org.hl7.gravity.refimpl.sdohexchange.exception.TaskUpdateException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskUpdateBundleExtractor.TaskUpdateInfoHolder;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Transaction bundle parser of resources required for Task update.
 */
public class TaskUpdateBundleExtractor extends BundleExtractor<TaskUpdateInfoHolder> {

  private final String id;

  public TaskUpdateBundleExtractor(String id) {
    this.id = id;
  }

  @Override
  public TaskUpdateInfoHolder extract(Bundle bundle) {
    return new TaskUpdateInfoHolder(extractToMap(bundle));
  }

  @Getter
  public class TaskUpdateInfoHolder {

    private final Task task;
    private final ServiceRequest serviceRequest;
    private final Organization ownerOrganization;
    private final Endpoint endpoint;

    public TaskUpdateInfoHolder(Map<? extends Class<? extends Resource>, List<Resource>> resources) {
      this.task = resourceList(resources, Task.class).stream()
          .findFirst()
          .orElseThrow(() -> new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id)));
      this.serviceRequest = resourceList(resources, ServiceRequest.class).stream()
          .findFirst()
          .orElseThrow(() -> new TaskUpdateException("ServiceRequest not found"));
      this.ownerOrganization = resourceList(resources, Organization.class).stream()
          .findFirst()
          .orElseThrow(() -> new TaskUpdateException("Owner Organization not found."));
      EndpointConnectionType connectionType = EndpointConnectionType.HL7FHIRREST;
      this.endpoint = resourceList(resources, Endpoint.class).stream()
          .findFirst()
          .filter(endpoint -> endpoint.getConnectionType()
              .getSystem()
              .equals(connectionType.getSystem()))
          .filter(endpoint -> endpoint.getConnectionType()
              .getCode()
              .equals(connectionType.toCode()))
          .filter(endpoint -> StringUtils.hasText(endpoint.getAddress()))
          .orElseThrow(() -> new TaskUpdateException("Endpoint not found."));
    }
  }
}