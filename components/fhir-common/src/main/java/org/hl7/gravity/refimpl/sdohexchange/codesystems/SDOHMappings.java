package org.hl7.gravity.refimpl.sdohexchange.codesystems;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Resource;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class SDOHMappings {

  private String system;
  private List<Category> categories;

  public Category getCategory(String code) {
    return categories.stream()
        .filter(c -> c.getCode()
            .equals(code))
        .findFirst()
        .orElseThrow(() -> new FHIRException("Unknown SDOHDomainCode code '" + code + "'"));
  }

  public List<System> getSystems(String code, Class<? extends Resource> resource) {
    return getCategory(code).getResource(resource)
        .getSystems();
  }

  public Coding findCategory(String code) {
    return categories.stream()
        .filter(category -> category.getCode()
            .equals(code))
        .map(category -> new Coding(system, category.getCode(), category.getDisplay()))
        .findFirst()
        .orElseThrow(() -> new FHIRException("Unknown SDOHDomainCode code '" + code + "'"));
  }

  public Coding findCoding(Class<? extends Resource> resource, String code) {
    return categories.stream()
        .map(c -> c.getResource(resource)
            .getSystems())
        .flatMap(List::stream)
        .map(system -> system.findCoding(code))
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() -> new FHIRException("Unknown '" + resource.getSimpleName() + "' code '" + code + "'"));
  }
}