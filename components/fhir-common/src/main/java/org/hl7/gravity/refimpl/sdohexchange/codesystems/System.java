package org.hl7.gravity.refimpl.sdohexchange.codesystems;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class System {

  private String system;
  private List<Coding> codings;

  public org.hl7.fhir.r4.model.Coding findCoding(String code) {
    return codings.stream()
        .filter(coding -> coding.getCode()
            .equals(code))
        .map(coding -> new org.hl7.fhir.r4.model.Coding(system, coding.getCode(), coding.getDisplay()))
        .findFirst()
        .orElse(null);
  }
}