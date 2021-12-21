package org.hl7.gravity.refimpl.sdohexchange.codes;

import com.google.common.base.Strings;
import org.hl7.fhir.exceptions.FHIRException;

public enum OrganizationTypeCode {
  CP,
  CBO;

  public static final String SYSTEM = "http://hl7.org/gravity/CodeSystem/sdohcc-temporary-organization-type-codes";

  public static OrganizationTypeCode fromCode(String codeString) throws FHIRException {
    if (Strings.isNullOrEmpty(codeString)) {
      return null;
    }
    if ("cp".equals(codeString)) {
      return CP;
    } else if ("cbo".equals(codeString)) {
      return CBO;
    }
    throw new FHIRException("Unknown OrganizationTypeCode code '" + codeString + "'");
  }

  public String toCode() {
    switch (this) {
      case CP:
        return "cp";
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
      case CP:
        return "Coordination Platform";
      case CBO:
        return "Community-based organization";
      default:
        return "?";
    }
  }

}