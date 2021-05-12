package org.hl7.gravity.refimpl.sdohexchange.dao;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.BaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Mykhailo Stefantsiv
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public abstract class FhirRepository<T extends IBaseResource> implements GenericRepository<T> {

  private final IGenericClient ehrClient;

  @Override
  public Optional<T> find(String id) {
    T resource = null;
    try{
    resource = ehrClient.read()
        .resource(getResourceType())
        .withId(id)
        .execute();
    } catch (ResourceNotFoundException e){
      log.warn(e.getMessage());
    }
    return Optional.ofNullable(resource);
  }

  @Override
  public Bundle find(String id, Collection includes) {
    IQuery<IBaseBundle> bundle = ehrClient.search()
        .forResource(getResourceType())
        .where(BaseResource.RES_ID.exactly()
            .codes(id));
    //TODO: fix this cast
    for (Object include : includes) {
      bundle = bundle.include((Include) include);
    }
    return bundle.returnBundle(Bundle.class)
        .execute();
  }

  @Override
  public Bundle find(List ids) {
    return ehrClient.search()
        .forResource(getResourceType())
        .where(BaseResource.RES_ID.exactly()
            .codes(ids))
        .returnBundle(Bundle.class)
        .execute();
  }

  public abstract Class<T> getResourceType();
}
