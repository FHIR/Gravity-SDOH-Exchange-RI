package org.hl7.gravity.refimpl.sdohexchange.codes;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Coding;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum GenderIdentityCode {

  MALE_TO_FEMALE("407376001", "Male-to-female transsexual (finding)"),
  FEMALE_TO_MALE("407377005", "Female-to-male transsexual (finding)"),
  NON_CONFIRMING("446131000124102", "Identifies as non-conforming gender (finding)"),
  AS_FEMALE("446141000124107", "Identifies as female gender (finding)"),
  AS_MALE("446151000124109", "Identifies as male gender (finding)"),
  ASKED_BUT_UNKNOWN("ASKU", "Asked but unknown"),
  OTHER("OTH", "Other");

  private final String code;
  private final String display;

  public static final String SNOMED_SYSTEM = "urn:oid:2.16.840.1.113883.6.96";
  public static final String NULL_FLAVOR_SYSTEM = "urn:oid:2.16.840.1.113883.5.1008";

  @JsonCreator
  public static GenderIdentityCode fromCode(String codeString) throws FHIRException {
    return Stream.of(values())
        .filter(targetEnum -> targetEnum.code.equals(codeString))
        .findFirst()
        .orElseThrow(() -> new FHIRException(String.format("Unsupported Gender Identity code '%s'", codeString)));
  }

  public Coding toCoding() {
    switch (this) {
      case MALE_TO_FEMALE:
      case FEMALE_TO_MALE:
      case NON_CONFIRMING:
      case AS_FEMALE:
      case AS_MALE:
        return new Coding(SNOMED_SYSTEM, getCode(), getDisplay());
      case ASKED_BUT_UNKNOWN:
      case OTHER:
        return new Coding(NULL_FLAVOR_SYSTEM, getCode(), getDisplay());
      default:
        return null;
    }
  }
}
