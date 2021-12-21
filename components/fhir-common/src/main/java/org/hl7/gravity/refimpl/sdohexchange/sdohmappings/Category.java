package org.hl7.gravity.refimpl.sdohexchange.sdohmappings;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Category extends Coding {

  private List<Resource> resources;

  public Resource findResource(Class<? extends org.hl7.fhir.r4.model.Resource> resourceClass) {
    return resources.stream()
        .filter(resource -> resource.getResource()
            .equals(resourceClass.getSimpleName()))
        .findFirst()
        .orElse(null);
  }
}