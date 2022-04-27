package org.hl7.gravity.refimpl.sdohexchange.dto.request.characteristic;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.exception.UnknownCodeException;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum CharacteristicCode {

  PERSONAL_PRONOUNS("90778-2", "Personal pronouns - Reported"),
  ETHNICITY("69490-1", "Ethnicity OMB.1997"),
  RACE("72826-1", "Race OMB.1997"),
  SEX_GENDER("99502-7", "Recorded sex or gender"),
  SEXUAL_ORIENTATION("76690-7", "Sexual orientation"),
  GENDER_IDENTITY("76691-5", "Gender identity");

  private final String code;
  private final String display;

  public static final String SYSTEM = "http://loinc.org";

  @JsonCreator
  public static CharacteristicCode fromText(String value) {
    return Stream.of(CharacteristicCode.values())
        .filter(targetEnum -> targetEnum.display.equals(value))
        .findFirst()
        .orElseThrow(() -> new UnknownCodeException(String.format("Unsupported Characteristic code '%s'", value)));
  }
}
