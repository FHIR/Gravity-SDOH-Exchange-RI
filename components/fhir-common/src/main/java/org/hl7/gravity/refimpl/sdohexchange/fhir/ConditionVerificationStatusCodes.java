package org.hl7.gravity.refimpl.sdohexchange.fhir;

import org.hl7.fhir.exceptions.FHIRException;

public enum ConditionVerificationStatusCodes {
  UNCONFIRMED,
  PROVISIONAL,
  DIFFERENTIAL,
  CONFIRMED,
  REFUTED,
  ENTERED_IN_ERROR;

  public static ConditionVerificationStatusCodes fromCode(String codeString) throws FHIRException {
    if (codeString == null || "".equals(codeString)) {
      return null;
    }
    if ("unconfirmed".equals(codeString)) {
      return UNCONFIRMED;
    }
    if ("provisional".equals(codeString)) {
      return PROVISIONAL;
    }
    if ("differential".equals(codeString)) {
      return DIFFERENTIAL;
    }
    if ("confirmed".equals(codeString)) {
      return CONFIRMED;
    }
    if ("refuted".equals(codeString)) {
      return REFUTED;
    }
    if ("entered-in-error".equals(codeString)) {
      return ENTERED_IN_ERROR;
    }
    throw new FHIRException("Unknown Condition Verification Status code '" + codeString + "'");
  }

  public String toCode() {
    switch (this) {
      case UNCONFIRMED:
        return "unconfirmed";
      case PROVISIONAL:
        return "provisional";
      case DIFFERENTIAL:
        return "differential";
      case CONFIRMED:
        return "confirmed";
      case REFUTED:
        return "refuted";
      case ENTERED_IN_ERROR:
        return "entered-in-error";
      default:
        return "?";
    }
  }
  public String getSystem() {
    switch (this) {
      case UNCONFIRMED:
        return "http://terminology.hl7.org/CodeSystem/condition-ver-status";
      case PROVISIONAL:
        return "http://terminology.hl7.org/CodeSystem/condition-ver-status";
      case DIFFERENTIAL:
        return "http://terminology.hl7.org/CodeSystem/condition-ver-status";
      case CONFIRMED:
        return "http://terminology.hl7.org/CodeSystem/condition-ver-status";
      case REFUTED:
        return "http://terminology.hl7.org/CodeSystem/condition-ver-status";
      case ENTERED_IN_ERROR:
        return "http://terminology.hl7.org/CodeSystem/condition-ver-status";
      default:
        return "?";
    }
  }

  public String getDefinition() {
    switch (this) {
      case UNCONFIRMED:
        return "There is not sufficient diagnostic and/or clinical evidence to treat this as a confirmed condition.";
      case PROVISIONAL:
        return "This is a tentative diagnosis - still a candidate that is under consideration.";
      case DIFFERENTIAL:
        return "One of a set of potential (and typically mutually exclusive) diagnoses asserted to further guide the diagnostic process and preliminary treatment.";
      case CONFIRMED:
        return "There is sufficient diagnostic and/or clinical evidence to treat this as a confirmed condition.";
      case REFUTED:
        return "This condition has been ruled out by diagnostic and clinical evidence.";
      case ENTERED_IN_ERROR:
        return "The statement was entered in error and is not valid.";
      default:
        return "?";
    }
  }

  public String getDisplay() {
    switch (this) {
      case UNCONFIRMED:
        return "Unconfirmed";
      case PROVISIONAL:
        return "Provisional";
      case DIFFERENTIAL:
        return "Differential";
      case CONFIRMED:
        return "Confirmed";
      case REFUTED:
        return "Refuted";
      case ENTERED_IN_ERROR:
        return "Entered in Error";
      default:
        return "?";
    }
  }
}