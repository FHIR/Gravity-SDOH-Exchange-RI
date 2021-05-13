package org.hl7.gravity.refimpl.sdohexchange.codesystems;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.exceptions.FHIRException;

import java.util.List;

@Data
@NoArgsConstructor
public class Category extends Coding {

  private List<Resource> resources;

  public Resource getResource(Class<? extends org.hl7.fhir.r4.model.Resource> resource) {
    return resources.stream()
        .filter(r -> r.getResource()
            .equals(resource.getSimpleName()))
        .findFirst()
        .orElseThrow(() -> new FHIRException("Unknown '" + resource.getSimpleName() + "' resource."));
  }
}