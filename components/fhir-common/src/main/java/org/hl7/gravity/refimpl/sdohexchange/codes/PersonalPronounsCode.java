package org.hl7.gravity.refimpl.sdohexchange.codes;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Coding;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum PersonalPronounsCode {

  HE_HIM("LA29518-0", "he/him/his/his/himself"),
  SHE_HER("LA29519-8", "she/her/her/hers/herself"),
  THEY_THEM("LA29520-6", "they/them/their/theirs/themselves"),
  OTHER("OTH", "other"),
  UNKNOWN("UNK", "unknown");

  private final String code;
  private final String display;

  public static final String LOINC_SYSTEM = "http://loinc.org";
  public static final String NULL_FLAVOR_SYSTEM = "http://terminology.hl7.org/CodeSystem/v3-NullFlavor";

  @JsonCreator
  public static PersonalPronounsCode fromCode(String codeString) throws FHIRException {
    return Stream.of(PersonalPronounsCode.values())
        .filter(targetEnum -> targetEnum.code.equals(codeString))
        .findFirst()
        .orElseThrow(() -> new FHIRException(String.format("Unsupported Personal pronouns type '%s'", codeString)));
  }

  public Coding toCoding() {
    switch (this) {
      case HE_HIM:
      case SHE_HER:
      case THEY_THEM:
        return new Coding(LOINC_SYSTEM, getCode(), getDisplay());
      case OTHER:
      case UNKNOWN:
        return new Coding(NULL_FLAVOR_SYSTEM, getCode(), getDisplay());
      default:
        return null;
    }
  }
}
