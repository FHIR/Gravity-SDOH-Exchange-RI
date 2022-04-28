package org.hl7.gravity.refimpl.sdohexchange.codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EthnicityCategory {

  HISPANIC_OR_LATINO("2135-2", "Hispanic or Latino"),
  NON_HISPANIC_OR_LATINO("2186-5", "Non Hispanic or Latino");

  private final String code;
  private final String display;

  public static final String SYSTEM = "urn:oid:2.16.840.1.113883.6.238";
}
