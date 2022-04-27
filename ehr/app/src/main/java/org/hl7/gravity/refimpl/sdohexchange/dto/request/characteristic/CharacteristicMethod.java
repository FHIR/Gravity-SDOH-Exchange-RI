package org.hl7.gravity.refimpl.sdohexchange.dto.request.characteristic;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.exception.UnknownCodeException;

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
  public static CharacteristicMethod fromText(String value) {
    return Stream.of(CharacteristicMethod.values())
        .filter(targetEnum -> targetEnum.display.equals(value))
        .findFirst()
        .orElseThrow(() -> new UnknownCodeException(String.format("Unsupported Observation method '%s'", value)));
  }
}
