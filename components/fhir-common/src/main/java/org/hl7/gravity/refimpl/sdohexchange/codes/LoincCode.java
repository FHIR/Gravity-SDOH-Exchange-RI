package org.hl7.gravity.refimpl.sdohexchange.codes;

import lombok.Getter;

/**
 * @author Mykhailo Stefantsiv
 */
@Getter
public enum LoincCode {
  HIGHEST_EDUCATION_LEVEL("82589-3");

  private String code;

  LoincCode(String code){
    this.code = code;
  }

  public static final String SYSTEM = "http://loinc.org";
}
