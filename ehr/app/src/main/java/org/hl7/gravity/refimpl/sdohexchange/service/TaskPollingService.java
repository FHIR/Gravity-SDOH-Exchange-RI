package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import com.google.common.base.Strings;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.EndpointConnectionType;
import org.hl7.fhir.r4.model.codesystems.SearchModifierCode;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.service.CpTaskUpdateService.CpTaskUpdateException;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class TaskPollingService {

  private final IGenericClient openEhrClient;
  private final CpTaskUpdateService cpTaskUpdateService;

  @Scheduled(fixedDelayString = "${scheduling.task-polling-delay-millis}")
  public void updateTasks() {
    log.info("Updating tasks from CP Organizations...");
    Bundle tasksBundle = openEhrClient.search()
        .forResource(Task.class)
        .include(Task.INCLUDE_OWNER)
        .include(Organization.INCLUDE_ENDPOINT.setRecurse(true))
        // Get only tasks sent to CP
        .where(new TokenClientParam("owner:Organization.type").exactly()
            .systemAndCode(OrganizationTypeCode.CP.getSystem(), OrganizationTypeCode.CP.toCode()))
        // Get only tasks in-progress
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.FAILED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.REJECTED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.COMPLETED.toCode()))
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.CANCELLED.toCode()))
        .where(Task.AUTHORED_ON.before()
            .millis(Date.from(LocalDateTime.now()
                .minusSeconds(15)
                .atZone(ZoneId.systemDefault())
                .toInstant())))
        .returnBundle(Bundle.class)
        .execute();

    // Retrieve all Task.focus Organization instances
    Map<String, Organization> organizationMap = FhirUtil.getFromBundle(tasksBundle, Organization.class)
        .stream()
        .collect(Collectors.toMap(r -> r.getIdElement()
            .toUnqualifiedVersionless()
            .getIdPart(), Function.identity()));

    // Retrieve all Task.focus Endpoint instances
    Map<String, Endpoint> endpointMap = FhirUtil.getFromBundle(tasksBundle, Endpoint.class)
        .stream()
        .filter(e -> e.getConnectionType()
            .getCode()
            .equals(EndpointConnectionType.HL7FHIRREST.toCode()))
        .collect(Collectors.toMap(r -> r.getIdElement()
            .toUnqualifiedVersionless()
            .getIdPart(), Function.identity()));

    List<Task> tasks = FhirUtil.getFromBundle(tasksBundle, Task.class);
    //Collect all entries from every Task bundle for performance considerations.
    Bundle updateBundle = new Bundle();
    updateBundle.setType(Bundle.BundleType.TRANSACTION);
    for (Task task : tasks) {
      updateBundle.getEntry()
          .addAll(getUpdateBundle(openEhrClient, task, organizationMap, endpointMap).getEntry());
    }
    //If there is at least one bundle entry - execute a transaction request.
    if (updateBundle.getEntry()
        .size() != 0) {
      log.info("One or more tasks were changed. Storing updates to EHR...");
      openEhrClient.transaction()
          .withBundle(updateBundle)
          .execute();
    }
    log.info("Task update process finished.");
  }

  protected Bundle getUpdateBundle(IGenericClient client, Task task, Map<String, Organization> organizationMap,
      Map<String, Endpoint> endpointMap) {
    String orgId = task.getOwner()
        .getReferenceElement()
        .getIdPart();
    // Organization will be always returned since it is included in a query. Tasks without an organization will no be
    // returned. No check needed.
    Organization organization = organizationMap.get(orgId);
    String endpointId = organization.getEndpoint()
        .stream()
        .map(ref -> ref.getReferenceElement()
            .getIdPart())
        .filter(endpointMap::containsKey)
        .findFirst()
        .orElse(null);

    Endpoint endpoint;
    if (endpointId == null) {
      return failTask(task,
          String.format("Organization resource with id '%s' does not contain endpoint of type '%s'.", orgId,
              EndpointConnectionType.HL7FHIRREST));
    } else {
      endpoint = endpointMap.get(endpointId);
      if (Strings.isNullOrEmpty(endpoint.getAddress())) {
        return failTask(task, String.format("Endpoint resource with id '%s' does not contain an address.", endpointId));
      }
    }
    try {
      return cpTaskUpdateService.getUpdateBundle(task, client, endpoint);
    } catch (CpTaskUpdateException exc) {
      return failTask(task, exc.getMessage());
    }
  }

  private Bundle failTask(Task task, String reason) {
    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);

    task.setStatus(Task.TaskStatus.FAILED);
    task.setStatusReason(new CodeableConcept().setText(reason));

    log.warn("Setting status FAILED for Task '" + task.getIdElement()
        .getIdPart() + "'. Reason: " + reason);
    bundle.addEntry(FhirUtil.createPutEntry(task));
    return bundle;
  }
}