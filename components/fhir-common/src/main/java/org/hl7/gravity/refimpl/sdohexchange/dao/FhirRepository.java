package org.hl7.gravity.refimpl.sdohexchange.dao;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.BaseResource;
import org.hl7.fhir.r4.model.Bundle;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Mykhailo Stefantsiv
 */
@Slf4j
public abstract class FhirRepository<T extends IBaseResource> implements GenericRepository<T> {

  @Getter(AccessLevel.PROTECTED)
  private final IGenericClient client;

  public FhirRepository(IGenericClient client) {
    this.client = client;
  }

  @Override
  public Optional<T> find(String id) {
    T resource = null;
    try {
      resource = client.read()
          .resource(getResourceType())
          .withId(id)
          .execute();
    } catch (ResourceNotFoundException e) {
      log.warn(e.getMessage());
    }
    return Optional.ofNullable(resource);
  }

  @Override
  public Bundle find(String id, Collection<Include> includes) {
    return find(id, includes, Collections.emptyList());
  }

  @Override
  public Bundle find(String id, Collection<Include> includes, Collection<Include> revIncludes) {
    IQuery<IBaseBundle> bundle = client.search()
        .forResource(getResourceType())
        .where(BaseResource.RES_ID.exactly()
            .codes(id));
    for (Include include : includes) {
      bundle = bundle.include(include);
    }
    for (Include revInclude : revIncludes) {
      bundle = bundle.revInclude(revInclude);
    }
    return bundle.returnBundle(Bundle.class)
        .execute();
  }

  @Override
  public Bundle find(List<String> ids) {
    return client.search()
        .forResource(getResourceType())
        .where(BaseResource.RES_ID.exactly()
            .codes(ids))
        .returnBundle(Bundle.class)
        .execute();
  }

  @Override
  public Bundle transaction(Bundle transaction) {
    return client.transaction()
        .withBundle(transaction)
        .execute();
  }

  public abstract Class<T> getResourceType();
}
