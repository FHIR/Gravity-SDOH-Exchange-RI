package org.hl7.gravity.refimpl.sdohexchange.codes;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Coding;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum EthnicityCode {

  HISPANIC_OR_LATINO("2135-2", "Hispanic or Latino"),
  NON_HISPANIC_OR_LATINO("2186-5", "Non Hispanic or Latino");

  private final String code;
  private final String display;

  public static final String SYSTEM = "urn:oid:2.16.840.1.113883.6.238";

  @JsonCreator
  public static EthnicityCode fromCode(String codeString) throws FHIRException {
    return Stream.of(EthnicityCode.values())
        .filter(targetEnum -> targetEnum.code.equals(codeString))
        .findFirst()
        .orElseThrow(() -> new FHIRException(String.format("Unsupported Ethnicity code '%s'", codeString)));
  }

  public Coding toCoding() {
    return new Coding(SYSTEM, getCode(), getDisplay());
  }
}
