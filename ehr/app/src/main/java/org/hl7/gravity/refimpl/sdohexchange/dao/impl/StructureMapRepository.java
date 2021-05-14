package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.StructureMap;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class StructureMapRepository extends FhirRepository<StructureMap> {

  public StructureMapRepository(IGenericClient ehrClient) {
    super(ehrClient);
  }

  public Optional<StructureMap> findByUrl(String mapUrl) {
    Bundle bundle = getEhrClient().search()
        .forResource(StructureMap.class)
        .where(StructureMap.URL.matches()
            .value(mapUrl))
        .returnBundle(Bundle.class)
        .execute();
    return Optional.ofNullable(FhirUtil.getFromBundle(bundle, StructureMap.class)
        .stream()
        .findFirst()
        .orElse(null));
  }

  @Override
  public Class<StructureMap> getResourceType() {
    return StructureMap.class;
  }
}
