package org.hl7.gravity.refimpl.sdohexchange.fhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.BaseReference;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.stereotype.Component;

@Component
public class ResourceLoader {

  public Map<Class<? extends Resource>, List<Resource>> getResources(IGenericClient client,
      List<Reference> references) {
    if (references.isEmpty()) {
      return Collections.emptyMap();
    }
    Bundle loadResourcesBundle = new Bundle();
    loadResourcesBundle.setType(BundleType.TRANSACTION);

    references.stream()
        .map(BaseReference::getReferenceElement)
        .map(element -> element.getResourceType() + "/" + element.getIdPart())
        .map(FhirUtil::createGetEntry)
        .forEach(loadResourcesBundle::addEntry);

    return client.transaction()
        .withBundle(loadResourcesBundle)
        .execute()
        .getEntry()
        .stream()
        .map(BundleEntryComponent::getResource)
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(Resource::getClass));
  }

  public Map<Class<? extends Resource>, List<Resource>> getResourcesBySystem(IGenericClient client,
      String system, List<Reference> references) {
    if (references.isEmpty()) {
      return Collections.emptyMap();
    }
    Bundle loadResourcesBundle = new Bundle();
    loadResourcesBundle.setType(Bundle.BundleType.TRANSACTION);

    references.stream()
        .map(BaseReference::getReferenceElement)
        .collect(Collectors.groupingBy(IIdType::getResourceType))

        .forEach((resourceType, referenceElements) -> {
          StringBuilder urlBuilder = new StringBuilder(resourceType)
              .append("?")
              .append("identifier")
              .append("=");
          String searchParams = referenceElements.stream()
              .map(element -> new StringBuilder(system)
                  .append("|")
                  .append(element.getIdPart()))
              .map(StringBuilder::toString)
              .collect(Collectors.joining(","));
          urlBuilder.append(searchParams);

          loadResourcesBundle.addEntry(FhirUtil.createGetEntry(urlBuilder.toString()));
        });

    return client.transaction()
        .withBundle(loadResourcesBundle)
        .execute()
        .getEntry()
        .stream()
        .map(Bundle.BundleEntryComponent::getResource)
        .filter(Bundle.class::isInstance)
        .map(Bundle.class::cast)
        .map(Bundle::getEntry)
        .flatMap(List::stream)
        .map(Bundle.BundleEntryComponent::getResource)
        .collect(Collectors.groupingBy(Resource::getClass));
  }
}