package org.hl7.gravity.refimpl.sdohexchange.fhir.reference;

import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

import java.util.List;
import java.util.Map;

/**
 * Provide behavior to resolve all references between external and local fhir servers.
 */
public interface ReferenceResolver {

  /**
   * Returns references to local fhir resources.
   */
  List<Reference> getLocalReferences();

  /**
   * Returns references to external fhir resources.
   *
   * @return external references
   */
  List<Reference> getExternalReferences();

  /**
   * Set and resolve local resources and references.
   *
   * @param localResources local resource map
   */
  void setLocalResources(Map<Class<? extends Resource>, List<Resource>> localResources);

  /**
   * Set and resolve external resources and references.
   *
   * @param externalResources external resource map
   */
  void setExternalResources(Map<Class<? extends Resource>, List<Resource>> externalResources);
}
