package org.hl7.gravity.refimpl.sdohexchange.codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Mykhailo Stefantsiv
 */
@Getter
@RequiredArgsConstructor
public enum SDOHTemporaryCode {
  EMPLOYMENT_STATUS("employment-status", "Employment Status"),
  CONTACT_CODE("contact-entity", "Contact Entity");

  private final String code;
  private final String display;

  public static final String SYSTEM =
      "http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/SDOHCC-CodeSystemTemporaryCodes";
}
