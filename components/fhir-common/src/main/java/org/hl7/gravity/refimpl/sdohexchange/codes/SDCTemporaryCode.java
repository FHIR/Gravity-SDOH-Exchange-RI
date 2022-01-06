package org.hl7.gravity.refimpl.sdohexchange.codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SDCTemporaryCode {
  QUESTIONNAIRE("questionnaire", "Questionnaire"),
  QUESTIONNAIRE_RESPONSE("questionnaire-response", "Questionnaire Response");

  private final String code;
  private final String display;

  public static final String SYSTEM = "http://hl7.org/fhir/uv/sdc/CodeSystem/temp";
}
