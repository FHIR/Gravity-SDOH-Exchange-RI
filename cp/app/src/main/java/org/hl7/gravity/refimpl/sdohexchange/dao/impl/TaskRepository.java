package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.SearchModifierCode;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.springframework.stereotype.Component;

/**
 * @author yuriy.flyud
 */
@Component
public class TaskRepository extends FhirRepository<Task> {

  public TaskRepository(IGenericClient cpClient) {
    super(cpClient);
  }

  public Bundle findAllTasks() {
    return getClient().search()
        .forResource(getResourceType())
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.REQUESTED.toCode()))
        //Filler order are CBO (Our) tasks, we should skip them here. This will probably be changed in the future.
        // For now this is a simple way to distinguish CP tasks from CBO tasks.
        .and(new TokenClientParam(Task.SP_INTENT + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskIntent.FILLERORDER.toCode()))
        // include ServiceRequest
        .include(Task.INCLUDE_FOCUS)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .returnBundle(Bundle.class)
        .execute();
  }

  public Bundle findAllOurTasks() {
    return getClient().search()
        .forResource(getResourceType())
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(Task.TaskStatus.REQUESTED.toCode()))
        //Filler order are CBO (Our) tasks. This will probably be changed in the future.
        // For now this is a simple way to distinguish CP tasks from CBO tasks.
        .and(Task.INTENT.exactly()
            .code(Task.TaskIntent.FILLERORDER.toCode()))
        // include ServiceRequest
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