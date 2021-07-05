package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Resource;

public abstract class BundleExtractor<T> {

  public abstract T extract(Bundle bundle);

  protected <R extends Resource> List<R> resourceList(
      Map<? extends Class<? extends Resource>, List<Resource>> resources,
      Class<R> resourceClass) {
    return Optional.ofNullable(resources.get(resourceClass))
        .orElse(Collections.emptyList())
        .stream()
        .map(resourceClass::cast)
        .collect(Collectors.toList());
  }

  protected Map<? extends Class<? extends Resource>, List<Resource>> extractToMap(Bundle bundle) {
    return bundle.getEntry()
        .stream()
        .map(BundleEntryComponent::getResource)
        .filter(Objects::nonNull)
        .map(resource -> {
          if (resource instanceof Bundle) {
            return ((Bundle) resource).getEntry()
                .stream()
                .map(BundleEntryComponent::getResource)
                .collect(Collectors.toList());
          } else {
            return Collections.singletonList(resource);
          }
        })
        .flatMap(Collection::stream)
        .collect(Collectors.groupingBy(Resource::getClass));
  }
}
