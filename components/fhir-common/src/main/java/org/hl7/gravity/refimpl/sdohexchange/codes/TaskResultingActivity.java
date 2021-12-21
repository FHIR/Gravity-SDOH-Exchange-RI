package org.hl7.gravity.refimpl.sdohexchange.codes;

import com.google.common.base.Strings;
import org.hl7.fhir.exceptions.FHIRException;

public enum TaskResultingActivity {
  REFERRAL_TO_COMMUNITY_MEALS_SERVICE_PROCEDURE,
  TRANSPORTATION_CASE_MANAGEMENT_PROCEDURE,
  REFERRAL_TO_HOUSING_SERVICE_PROCEDURE;

  public static final String SYSTEM = "http://snomed.info/sct";

  public static TaskResultingActivity fromCode(String codeString) throws FHIRException {
    if (Strings.isNullOrEmpty(codeString)) {
      return null;
    }
    if ("713109004".equals(codeString)) {
      return REFERRAL_TO_COMMUNITY_MEALS_SERVICE_PROCEDURE;
    }
    if ("410365006".equals(codeString)) {
      return TRANSPORTATION_CASE_MANAGEMENT_PROCEDURE;
    }
    if ("710911006".equals(codeString)) {
      return REFERRAL_TO_HOUSING_SERVICE_PROCEDURE;
    }
    throw new FHIRException("Unknown TaskResultingActivity code '" + codeString + "'");
  }

  public String toCode() {
    switch (this) {
      case REFERRAL_TO_COMMUNITY_MEALS_SERVICE_PROCEDURE:
        return "713109004";
      case TRANSPORTATION_CASE_MANAGEMENT_PROCEDURE:
        return "410365006";
      case REFERRAL_TO_HOUSING_SERVICE_PROCEDURE:
        return "710911006";
      default:
        return "?";
    }
  }

  public String getSystem() {
    return SYSTEM;
  }

  public String getDisplay() {
    switch (this) {
      case REFERRAL_TO_COMMUNITY_MEALS_SERVICE_PROCEDURE:
        return "Referral to community meals service (procedure)";
      case TRANSPORTATION_CASE_MANAGEMENT_PROCEDURE:
        return "Transportation case management (procedure)";
      case REFERRAL_TO_HOUSING_SERVICE_PROCEDURE:
        return "Referral to housing service (procedure)";
      default:
        return "?";
    }
  }

}