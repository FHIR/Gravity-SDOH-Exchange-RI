package org.hl7.gravity.refimpl.sdohexchange.fhir.codes;

import lombok.Getter;

/**
 * @author Mykhailo Stefantsiv
 */
@Getter
public enum SDOHTemporaryCode {
  EMPLOYMENT_STATUS("employment-status");

  SDOHTemporaryCode(String code) {
    this.code = code;
  }

  private final String code;

  public static final String SYSTEM = "http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes";
}
