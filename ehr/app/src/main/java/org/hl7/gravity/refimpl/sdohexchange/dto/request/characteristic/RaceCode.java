package org.hl7.gravity.refimpl.sdohexchange.dto.request.characteristic;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.exceptions.FHIRException;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum RaceCode {

  AMERICAN_INDIAN_OR_ALASKA_NATIVE("1002-5", "American Indian or Alaska Native"),
  ASIAN("2028-9", "Asian"),
  BLACK_OR_AFRICAN_AMERICAN("2054-5", "Black or African American"),
  NATIVE_HAWAIIAN_OR_OTHER_PACIFIC_ISLANDER("2076-8", "Native Hawaiian or Other Pacific Islander"),
  WHITE("2106-3", "White");

  private final String code;
  private final String display;

  public static final String SYSTEM = "urn:oid:2.16.840.1.113883.6.238";

  @JsonCreator
  public static RaceCode fromCode(String codeString) throws FHIRException {
    return Stream.of(RaceCode.values())
        .filter(targetEnum -> targetEnum.code.equals(codeString))
        .findFirst()
        .orElseThrow(() -> new FHIRException(String.format("Unsupported Race code '%s'", codeString)));
  }
}
