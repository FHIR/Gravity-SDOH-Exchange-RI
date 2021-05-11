package org.hl7.gravity.refimpl.sdohexchange.dao;

import ca.uhn.fhir.model.api.Include;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;

import java.util.Collection;
import java.util.List;

/**
 * @author Mykhailo Stefantsiv
 */
public interface GenericRepository<T extends IBaseResource> {

  T findById(String id);

  Bundle findByIdWithIncludes(String id, Collection<Include> includes);

  Bundle findAllByIds(List<String> ids);
}
