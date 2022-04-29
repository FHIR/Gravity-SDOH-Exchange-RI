package org.hl7.gravity.refimpl.sdohexchange.codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Coding;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum CharacteristicMethod {

  SELF_REPORTED("self-reported", "Self Reported"),
  DERIVED_SPECIFY("derived-specify", "Derived Specify"),
  OTHER_SPECIFY("other-specify", "Other Specify");

  private final String code;
  private final String display;

  public static final String SYSTEM =
      "http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/SDOHCC-CodeSystemTemporaryCodes";

  public static CharacteristicMethod fromCode(String codeString) throws FHIRException {
    return Stream.of(CharacteristicMethod.values())
        .filter(targetEnum -> targetEnum.code.equals(codeString))
        .findFirst()
        .orElseThrow(() -> new FHIRException(String.format("Unsupported Characteristic method '%s'", codeString)));
  }

  public Coding toCoding() {
    return new Coding(SYSTEM, this.getCode(), this.getDisplay());
  }
}
