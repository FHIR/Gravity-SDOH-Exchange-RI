package org.hl7.gravity.refimpl.sdohexchange.dao;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.SearchModifierCode;

public class TaskRepository extends FhirRepository<Task> {

  private static final String orgName = "Social Alliance";

  public TaskRepository(IGenericClient client) {
    super(client);
  }

  public Bundle findAllTasks() {
    return getClient().search()
        .forResource(getResourceType())
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.REQUESTED.toCode()))
        .and(Task.INTENT.exactly()
            .code(Task.TaskIntent.FILLERORDER.toCode()))
        .and(new TokenClientParam(Task.SP_OWNER + ":" + "Organization." + Organization.SP_NAME + ":"
            + SearchModifierCode.EXACT.toCode()).exactly()
            .code(orgName))
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
