package org.hl7.gravity.refimpl.sdohexchange.codesystems;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class System {

  private String system;
  private List<Coding> codings;

  public org.hl7.fhir.r4.model.Coding findCoding(String codingCode) {
    return codings.stream()
        .filter(coding -> coding.getCode()
            .equals(codingCode))
        .map(this::toCoding)
        .findFirst()
        .orElse(null);
  }

  protected org.hl7.fhir.r4.model.Coding toCoding(Coding coding) {
    return new org.hl7.fhir.r4.model.Coding(system, coding.getCode(), coding.getDisplay());
  }
}