package org.hl7.gravity.refimpl.sdohexchange.dao;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.SearchModifierCode;

public class TaskRepository extends FhirRepository<Task> {

  private static final String orgId = "Organization/15573";

  public TaskRepository(IGenericClient client) {
    super(client);
  }

  public Bundle findAllTasks() {
    return getClient().search()
        .forResource(getResourceType())
        .where(new TokenClientParam(Task.SP_STATUS + ':' + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.REQUESTED.toCode()))
        .and(Task.INTENT.exactly()
            .code(Task.TaskIntent.FILLERORDER.toCode()))
        .and(Task.OWNER.hasId(orgId))
        .include(Task.INCLUDE_FOCUS)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .returnBundle(Bundle.class)
        .execute();
  }

  @Override
  public Class<Task> getResourceType() {
    return Task.class;
  }
}
