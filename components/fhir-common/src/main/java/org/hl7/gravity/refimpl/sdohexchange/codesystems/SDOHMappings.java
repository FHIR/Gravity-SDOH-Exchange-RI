package org.hl7.gravity.refimpl.sdohexchange.codesystems;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.gravity.refimpl.sdohexchange.exception.UnknownCategoryException;
import org.hl7.gravity.refimpl.sdohexchange.exception.UnknownCodeException;

@Data
@NoArgsConstructor
public class SDOHMappings {

  private String system;
  private List<Category> categories;

  protected Category findCategory(String categoryCode) {
    return categories.stream()
        .filter(category -> category.getCode()
            .equals(categoryCode))
        .findFirst()
        .orElseThrow(() -> new UnknownCategoryException("Unknown SDOHDomain category '" + categoryCode + "'"));
  }

  public List<org.hl7.gravity.refimpl.sdohexchange.codesystems.Coding> findAllResourceCodings(String categoryCode,
      Class<? extends org.hl7.fhir.r4.model.Resource> resourceClass) {
    return Optional.ofNullable(findCategory(categoryCode).findResource(resourceClass))
        .map(Resource::getSystems)
        .orElse(Collections.emptyList())
        .stream()
        .map(System::getCodings)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  public Coding findCategoryCoding(String categoryCode) {
    return toCategoryCoding(findCategory(categoryCode));
  }

  public Coding findResourceCoding(Class<? extends org.hl7.fhir.r4.model.Resource> resource, String codingCode) {
    return categories.stream()
        .map(category -> Optional.ofNullable(category.findResource(resource))
            .map(Resource::getSystems)
            .orElse(Collections.emptyList()))
        .flatMap(List::stream)
        .map(system -> system.findCoding(codingCode))
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() -> new UnknownCodeException(
            "Unknown '" + resource.getSimpleName() + "' code '" + codingCode + "'"));
  }

  protected Coding toCategoryCoding(Category category) {
    return new Coding(system, category.getCode(), category.getDisplay());
  }
}