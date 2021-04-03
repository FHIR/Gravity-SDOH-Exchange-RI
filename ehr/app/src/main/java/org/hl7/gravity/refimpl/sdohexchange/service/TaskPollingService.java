package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.EndpointConnectionType;
import org.hl7.fhir.r4.model.codesystems.SearchModifierCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.IGenericClientProvider;
import org.hl7.gravity.refimpl.sdohexchange.fhir.codesystems.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class TaskPollingService {

  private final IGenericClientProvider clientProvider;
  private final CbroTaskUpdateService cbroTaskUpdateService;
  @Value("${ehr.open-fhir-server-uri}")
  private String openFhirServerUri;

  @Scheduled(fixedDelayString = "${scheduling.task-polling-delay-millis}")
  public void updateTasks() {
    log.info("Updating tasks from CBRO Organizations...");
    IGenericClient client = clientProvider.client(openFhirServerUri, null);
    Bundle bundle = client.search()
        .forResource(Task.class)
        .include(Task.INCLUDE_OWNER)
        .include(Organization.INCLUDE_ENDPOINT.setRecurse(true))
        // Get only tasks sent to CBRO
        .where(new TokenClientParam("owner:Organization.type").exactly()
            .systemAndCode(OrganizationTypeCode.CBRO.getSystem(), OrganizationTypeCode.CBRO.toCode()))
        // Get only tasks in-progress
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.REQUESTED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.FAILED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.REJECTED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.COMPLETED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.CANCELLED.toCode()))
        .returnBundle(Bundle.class)
        .execute();

    // Retrieve all Task.focus Organization instances
    Map<String, Organization> orgMap = FhirUtil.getFromBundle(bundle, Organization.class)
        .stream()
        .collect(Collectors.toMap(r -> r.getIdElement()
            .toUnqualifiedVersionless()
            .getIdPart(), r -> r));

    // Retrieve all Task.focus Endpoint instances
    Map<String, Endpoint> endpointMap = FhirUtil.getFromBundle(bundle, Endpoint.class)
        .stream()
        .filter(e -> e.getConnectionType()
            .getCode()
            .equals(EndpointConnectionType.HL7FHIRREST.toCode()))
        .collect(Collectors.toMap(r -> r.getIdElement()
            .toUnqualifiedVersionless()
            .getIdPart(), r -> r));

    List<Task> tasks = FhirUtil.getFromBundle(bundle, Task.class);
    //Collect all entries from every Task bundle for performance considerations.
    Bundle b = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);
    for (Task t : tasks) {
      b.getEntry()
          .addAll(getUpdateBundle(client, t, orgMap, endpointMap).getEntry());
    }
    //If there is at least one bundle entry - execute a transaction request.
    if (b.getEntry()
        .size() != 0) {
      log.info("One or more tasks were changed. Storing updates to EHR...");
      client.transaction()
          .withBundle(b)
          .execute();
    }
    log.info("Task update process finished.");
  }

  protected Bundle getUpdateBundle(IGenericClient client, Task t, Map<String, Organization> orgMap,
      Map<String, Endpoint> epMap) {
    String orgId = t.getOwner()
        .getReferenceElement()
        .getIdPart();
    // Organization will be always returned since it is included in a query. Tasks without an organization will no be
    // returned. No check needed.
    Organization org = orgMap.get(orgId);
    String epId = org.getEndpoint()
        .stream()
        .map(ref -> ref.getReferenceElement()
            .getIdPart())
        .filter(id -> epMap.containsKey(id))
        .findFirst()
        .orElse(null);

    Endpoint ep;
    if (epId == null) {
      return failTask(t,
          String.format("Organization resource with id '%s' does not contain endpoint of type '%s'.", orgId,
              EndpointConnectionType.HL7FHIRREST));
    } else {
      ep = epMap.get(epId);
      if (Strings.isNullOrEmpty(ep.getAddress())) {
        return failTask(t, String.format("Endpoint resource with id '%s' does not contain an address.", epId));
      }
    }
    IGenericClient cbroClient = clientProvider.client(ep.getAddress(), null);
    try {
      return cbroTaskUpdateService.getUpdateBundle(t, client, cbroClient);
    } catch (CbroTaskUpdateService.CbroTaskUpdateException exc) {
      return failTask(t, exc.getMessage());
    }
  }

  private Bundle failTask(Task task, String reason) {
    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);

    task.setStatus(Task.TaskStatus.FAILED);
    task.setStatusReason(new CodeableConcept().setText(reason));

    log.warn("Setting status FAILED for Task '" + task.getIdElement()
        .getIdPart() + "'. Reason: " + reason);
    bundle.addEntry()
        .setResource(task)
        .setFullUrl(task.getIdElement()
            .getValue())
        .getRequest()
        .setUrl(task.getIdElement()
            .getValue())
        .setMethod(Bundle.HTTPVerb.PUT);
    return bundle;
  }
}