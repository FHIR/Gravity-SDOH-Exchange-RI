package org.hl7.gravity.refimpl.sdohexchange.codes;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.exceptions.FHIRException;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum SexGenderCode {

  F("LA13504-8", "F"),
  M("LA15170-6", "M"),
  X("LA32969-0", "X");

  private final String code;
  private final String display;

  public static final String SYSTEM = "http://loinc.org";

  @JsonCreator
  public static SexGenderCode fromCode(String codeString) throws FHIRException {
    return Stream.of(SexGenderCode.values())
        .filter(targetEnum -> targetEnum.code.equals(codeString))
        .findFirst()
        .orElseThrow(() -> new FHIRException(String.format("Unsupported Sex Gender code '%s'", codeString)));
  }
}
