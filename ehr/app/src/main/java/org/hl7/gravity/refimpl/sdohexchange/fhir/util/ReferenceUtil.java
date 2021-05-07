package org.hl7.gravity.refimpl.sdohexchange.fhir.util;

import lombok.experimental.UtilityClass;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Reference;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mykhailo Stefantsiv
 */
@UtilityClass
public final class ReferenceUtil {

  public static List<String> retrieveReferencedIds(List<Reference> references,
      Class<? extends IBaseResource> resource) {
    return references.stream()
        .filter(r -> getResourceType(r).equals(resource.getSimpleName()))
        .map(ReferenceUtil::getReferenceId)
        .collect(Collectors.toList());
  }

  public static String getResourceType(Reference reference) {
    String type = reference.getReferenceElement()
        .getResourceType();
    return type == null ? reference.getReferenceElement()
        .getValue() : type;
  }

  public static String getReferenceId(Reference reference) {
    return reference.getId() == null ? reference.getReference()
        .substring(reference.getReference()
            .indexOf('/') + 1) : reference.getId();
  }
}
