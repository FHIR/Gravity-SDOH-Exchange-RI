package org.hl7.gravity.refimpl.sdohexchange.fhir;

import org.hl7.fhir.exceptions.FHIRException;

public enum UsCoreConditionCategory {
  /**
   * An item on a problem list that can be managed over time and can be expressed by a practitioner (e.g. physician,
   * nurse), patient, or related person.
   */
  PROBLEMLISTITEM,
  /** A point in time diagnosis (e.g. from a physician or nurse) in context of an encounter. */
  ENCOUNTERDIAGNOSIS,
  /** Additional health concerns from other stakeholders which are outside the provider’s problem list. */
  HEALTHCONCERN;

  public static UsCoreConditionCategory fromCode(String codeString) throws FHIRException {
    if (codeString == null || "".equals(codeString)) {
      return null;
    }
    if ("problem-list-item".equals(codeString)) {
      return PROBLEMLISTITEM;
    }
    if ("encounter-diagnosis".equals(codeString)) {
      return ENCOUNTERDIAGNOSIS;
    }
    if ("health-concern".equals(codeString)) {
      return HEALTHCONCERN;
    }
    throw new FHIRException("Unknown ConditionCategory code '" + codeString + "'");
  }

  public String toCode() {
    switch (this) {
      case PROBLEMLISTITEM:
        return "problem-list-item";
      case ENCOUNTERDIAGNOSIS:
        return "encounter-diagnosis";
      case HEALTHCONCERN:
        return "health-concern";
      default:
        return "?";
    }
  }

  public String getSystem() {
    switch (this) {
      case PROBLEMLISTITEM:
        return "http://hl7.org/fhir/us/core/CodeSystem/condition-category";
      case ENCOUNTERDIAGNOSIS:
        return "http://hl7.org/fhir/us/core/CodeSystem/condition-category";
      case HEALTHCONCERN:
        return "http://hl7.org/fhir/us/core/CodeSystem/condition-category";
      default:
        return "?";
    }
  }

  public String getDefinition() {
    switch (this) {
      case PROBLEMLISTITEM:
        return "An item on a problem list that can be managed over time and can be expressed by a practitioner (e.g. "
            + "physician, nurse), patient, or related person.";
      case ENCOUNTERDIAGNOSIS:
        return "A point in time diagnosis (e.g. from a physician or nurse) in context of an encounter.";
      case HEALTHCONCERN:
        return "Additional health concerns from other stakeholders which are outside the provider’s problem list.";
      default:
        return "?";
    }
  }

  public String getDisplay() {
    switch (this) {
      case PROBLEMLISTITEM:
        return "Problem List Item";
      case ENCOUNTERDIAGNOSIS:
        return "Encounter Diagnosis";
      case HEALTHCONCERN:
        return "Health Concern";
      default:
        return "?";
    }
  }
}