package org.hl7.gravity.refimpl.sdohexchange.sdohmappings;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class System {

  public static final String SNOMED = "http://snomed.info/sct";
  public static final String ICD_10 = "http://hl7.org/fhir/sid/icd-10-cm";

  private String system;
  private String display;
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