package org.hl7.gravity.refimpl.sdohexchange.dto.request.characteristic;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.exceptions.FHIRException;

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

  @JsonCreator
  public static CharacteristicMethod fromCode(String codeString) throws FHIRException {
    return Stream.of(CharacteristicMethod.values())
        .filter(targetEnum -> targetEnum.code.equals(codeString))
        .findFirst()
        .orElseThrow(() -> new FHIRException(String.format("Unsupported Observation method '%s'", codeString)));
  }
}
