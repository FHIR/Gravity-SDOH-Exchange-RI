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
        //Intent=order are CP tasks, Filler-order are CBO (Our) tasks.
        .and(Task.INTENT.exactly()
            .code(Task.TaskIntent.ORDER.toCode()))
        // include ServiceRequest
        .include(Task.INCLUDE_FOCUS)
        .revInclude(Task.INCLUDE_BASED_ON)
        .include(Task.INCLUDE_OWNER.asRecursive())
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
        //Intent=order are CP tasks, Filler-order are CBO (Our) tasks.
        .and(Task.INTENT.exactly()
            .code(Task.TaskIntent.FILLERORDER.toCode()))
        // include ServiceRequest
        .include(Task.INCLUDE_FOCUS)
        .include(Task.INCLUDE_BASED_ON)
        .include(Task.INCLUDE_OWNER.asRecursive())
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .returnBundle(Bundle.class)
        .execute();
  }

  /**
   * Find our task (intent=filler-order) for a corresponding CP task.
   *
   * @param task CP task (intent=order)
   * @return a bundle with a filler-order Task and ServiceRequest
   */
  public Bundle findOurTask(Task task) {
    return getClient().search()
        .forResource(getResourceType())
        .where(Task.BASED_ON.hasId(task.getIdElement()
            .toUnqualifiedVersionless()))
        //Intent=order are CP tasks, Filler-order are CBO (Our) tasks.
        .and(Task.INTENT.exactly()
            .code(Task.TaskIntent.FILLERORDER.toCode()))
        // include ServiceRequest
        .include(Task.INCLUDE_FOCUS)
        .returnBundle(Bundle.class)
        .execute();
  }

  @Override
  public Class<Task> getResourceType() {
    return Task.class;
  }
}