package org.hl7.gravity.refimpl.sdohexchange.sdohmappings;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.sdohmappings.System;

@Data
@NoArgsConstructor
public class Resource {

  private String resource;
  private List<System> systems;

  public List<System> getSystems(List<String> systemNames) {
    return this.systems.stream()
        .filter(system -> systemNames.contains(system.getSystem()))
        .collect(Collectors.toList());
  }

  public System getSystem(String systemName) {
    return this.systems.stream()
        .filter(system -> system.getSystem()
            .equals(systemName))
        .findFirst()
        .orElse(null);
  }
}