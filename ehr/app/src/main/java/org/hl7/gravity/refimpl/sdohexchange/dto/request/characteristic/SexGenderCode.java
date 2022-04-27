package org.hl7.gravity.refimpl.sdohexchange.dto.request.characteristic;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.exception.UnknownCodeException;

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
  public static SexGenderCode fromText(String value) {
    return Stream.of(SexGenderCode.values())
        .filter(targetEnum -> targetEnum.display.equals(value))
        .findFirst()
        .orElseThrow(() -> new UnknownCodeException(String.format("Unsupported Sex Gender code '%s'", value)));
  }
}
