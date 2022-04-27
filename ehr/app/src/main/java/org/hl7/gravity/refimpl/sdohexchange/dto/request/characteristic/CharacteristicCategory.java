package org.hl7.gravity.refimpl.sdohexchange.dto.request.characteristic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CharacteristicCategory {

  PERSONAL_CHARACTERISTIC("personal-characteristic", "Personal Characteristic");

  private final String code;
  private final String display;

  public static final String SYSTEM =
      "http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/SDOHCC-CodeSystemTemporaryCodes";
}
