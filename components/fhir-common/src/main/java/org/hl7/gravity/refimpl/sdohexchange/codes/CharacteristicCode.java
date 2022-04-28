package org.hl7.gravity.refimpl.sdohexchange.codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Coding;

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

  public static CharacteristicCode fromCode(String codeString) throws FHIRException {
    return Stream.of(CharacteristicCode.values())
        .filter(targetEnum -> targetEnum.code.equals(codeString))
        .findFirst()
        .orElseThrow(() -> new FHIRException(String.format("Unsupported Characteristic code '%s'", codeString)));
  }

  public Coding toCoding() {
    return new Coding(SYSTEM, this.getCode(), this.getDisplay());
  }
}
