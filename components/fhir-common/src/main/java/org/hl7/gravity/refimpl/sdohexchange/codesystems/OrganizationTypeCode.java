package org.hl7.gravity.refimpl.sdohexchange.codesystems;

import com.google.common.base.Strings;
import org.hl7.fhir.exceptions.FHIRException;

public enum OrganizationTypeCode {
  CBRO,
  CBO;

  public static final String SYSTEM = "http://hl7.org/gravity/CodeSystem/sdohcc-temporary-organization-type-codes";

  public static OrganizationTypeCode fromCode(String codeString) throws FHIRException {
    if (Strings.isNullOrEmpty(codeString)) {
      return null;
    }
    if ("cbro".equals(codeString)) {
      return CBRO;
    } else if ("cbo".equals(codeString)) {
      return CBO;
    }
    throw new FHIRException("Unknown OrganizationTypeCode code '" + codeString + "'");
  }

  public String toCode() {
    switch (this) {
      case CBRO:
        return "cbro";
      case CBO:
        return "cbo";
      default:
        return "?";
    }
  }

  public String getSystem() {
    return SYSTEM;
  }

  public String getDisplay() {
    switch (this) {
      case CBRO:
        return "Community-based organization";
      case CBO:
        return "Community-based referral organization";
      default:
        return "?";
    }
  }

}