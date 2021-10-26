package org.hl7.gravity.refimpl.sdohexchange.dao;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.SearchModifierCode;

@Slf4j
public class TaskRepository extends FhirRepository<Task> {

  private final String applicationUrl;

  public TaskRepository(IGenericClient client, String applicationUrl) {
    super(client);
    this.applicationUrl = applicationUrl;
  }

  public Bundle findAllTasks() {
    Bundle bundle = getClient().search()
        .forResource(getResourceType())
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.REQUESTED.toCode()))
        .and(Task.INTENT.exactly()
            .code(Task.TaskIntent.FILLERORDER.toCode()))
        .and(new TokenClientParam(
            Task.SP_OWNER + ":" + Organization.class.getSimpleName() + "." + Organization.SP_IDENTIFIER).exactly()
            .code(applicationUrl))
        .include(Task.INCLUDE_FOCUS)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .returnBundle(Bundle.class)
        .execute();
    if (!bundle.hasEntry()) {
      log.info(String.format(
          "No filler-order tasks were found at server '%s'. Either there are no tasks, or Organization"
              + ".identifier does not match '%s'.", getClient().getServerBase(), applicationUrl));
    }
    return bundle;
  }

  @Override
  public Class<Task> getResourceType() {
    return Task.class;
  }
}
