package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.SearchModifierCode;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class TaskRepository extends FhirRepository<Task> {

  @Autowired
  public TaskRepository(IGenericClient cbroClient) {
    super(cbroClient);
  }

  public Bundle findAll(List<Task.TaskStatus> excludeStatuses) {
    IQuery<IBaseBundle> query = getClient().search()
        .forResource(Task.class);
    excludeStatuses.forEach(status -> query.where(new TokenClientParam(
        Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
        .code(status.toCode())));

    return query.sort()
        .descending(Constants.PARAM_LASTUPDATED)
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
