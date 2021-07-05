package org.hl7.gravity.refimpl.sdohexchange.fhir;

import org.hl7.fhir.exceptions.FHIRException;

public enum ConditionClinicalStatusCodes {
  ACTIVE,
  RECURRENCE,
  RELAPSE,
  INACTIVE,
  REMISSION,
  RESOLVED;

  public static ConditionClinicalStatusCodes fromCode(String codeString) throws FHIRException {
    if (codeString == null || "".equals(codeString)) {
      return null;
    }
    if ("active".equals(codeString)) {
      return ACTIVE;
    }
    if ("recurrence".equals(codeString)) {
      return RECURRENCE;
    }
    if ("relapse".equals(codeString)) {
      return RELAPSE;
    }
    if ("inactive".equals(codeString)) {
      return INACTIVE;
    }
    if ("remission".equals(codeString)) {
      return REMISSION;
    }
    if ("resolved".equals(codeString)) {
      return RESOLVED;
    }
    throw new FHIRException("Unknown Condition Clinical Status code '" + codeString + "'");
  }

  public String toCode() {
    switch (this) {
      case ACTIVE:
        return "active";
      case RECURRENCE:
        return "recurrence";
      case RELAPSE:
        return "relapse";
      case INACTIVE:
        return "inactive";
      case REMISSION:
        return "remission";
      case RESOLVED:
        return "resolved";
      default:
        return "?";
    }
  }

  public String getSystem() {
    switch (this) {
      case ACTIVE:
        return "http://terminology.hl7.org/CodeSystem/condition-clinical";
      case RECURRENCE:
        return "http://terminology.hl7.org/CodeSystem/condition-clinical";
      case RELAPSE:
        return "http://terminology.hl7.org/CodeSystem/condition-clinical";
      case INACTIVE:
        return "http://terminology.hl7.org/CodeSystem/condition-clinical";
      case REMISSION:
        return "http://terminology.hl7.org/CodeSystem/condition-clinical";
      case RESOLVED:
        return "http://terminology.hl7.org/CodeSystem/condition-clinical";
      default:
        return "?";
    }
  }

  public String getDefinition() {
    switch (this) {
      case ACTIVE:
        return "The subject is currently experiencing the symptoms of the condition or there is evidence of the "
            + "condition.";
      case RECURRENCE:
        return "The subject is experiencing a re-occurence or repeating of a previously resolved condition, e.g. "
            + "urinary tract infection, pancreatitis, cholangitis, conjunctivitis.";
      case RELAPSE:
        return "The subject is experiencing a return of a condition, or signs and symptoms after a period of "
            + "improvement or remission, e.g. relapse of cancer, multiple sclerosis, rheumatoid arthritis, systemic "
            + "lupus erythematosus, bipolar disorder, [psychotic relapse of] schizophrenia, etc.";
      case INACTIVE:
        return "The subject is no longer experiencing the symptoms of the condition or there is no longer evidence of"
            + " the condition.";
      case REMISSION:
        return "The subject is no longer experiencing the symptoms of the condition, but there is a risk of the "
            + "symptoms returning.";
      case RESOLVED:
        return "The subject is no longer experiencing the symptoms of the condition and there is a negligible "
            + "perceived risk of the symptoms returning.";
      default:
        return "?";
    }
  }

  public String getDisplay() {
    switch (this) {
      case ACTIVE:
        return "Active";
      case RECURRENCE:
        return "Recurrence";
      case RELAPSE:
        return "Relapse";
      case INACTIVE:
        return "Inactive";
      case REMISSION:
        return "Remission";
      case RESOLVED:
        return "Resolved";
      default:
        return "?";
    }
  }
}