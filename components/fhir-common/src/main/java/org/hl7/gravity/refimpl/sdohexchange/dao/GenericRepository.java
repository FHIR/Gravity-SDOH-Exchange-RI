package org.hl7.gravity.refimpl.sdohexchange.dao;

import ca.uhn.fhir.model.api.Include;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Mykhailo Stefantsiv
 */
public interface GenericRepository<T extends IBaseResource> {

  Optional<T> find(String id);

  Bundle find(String id, Collection<Include> includes);

  Bundle find(String id, Collection<Include> includes, Collection<Include> revIncludes);

  Bundle find(List<String> ids);

  Bundle transaction(Bundle transaction);
}
