package org.hl7.gravity.refimpl.sdohexchange.codesystems;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Resource {

  private String resource;
  private List<System> systems;
}