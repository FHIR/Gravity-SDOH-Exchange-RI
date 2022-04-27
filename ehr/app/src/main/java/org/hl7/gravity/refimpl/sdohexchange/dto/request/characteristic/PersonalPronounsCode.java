package org.hl7.gravity.refimpl.sdohexchange.dto.request.characteristic;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.exception.UnknownCodeException;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum PersonalPronounsCode {

  HE_HIM("LA29518-0", "he/him/his/his/himself"),
  SHE_HER("LA29519-8", "she/her/her/hers/herself"),
  THEY_THEM("LA29520-6", "they/them/their/theirs/themselves");

  private final String code;
  private final String display;

  public static final String SYSTEM = "http://loinc.org";

  @JsonCreator
  public static PersonalPronounsCode fromText(String value) {
    return Stream.of(PersonalPronounsCode.values())
        .filter(targetEnum -> targetEnum.display.equals(value))
        .findFirst()
        .orElseThrow(() -> new UnknownCodeException(String.format("Unsupported Personal pronouns type '%s'", value)));
  }
}
