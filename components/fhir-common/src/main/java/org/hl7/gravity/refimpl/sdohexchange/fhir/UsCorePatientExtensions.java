package org.hl7.gravity.refimpl.sdohexchange.fhir;

import lombok.experimental.UtilityClass;

/**
 * This profile sets minimum expectations for the Patient resource to record, search, and fetch basic demographics and
 * other administrative information about an individual patient. It identifies which core elements, extensions,
 * vocabularies and value sets SHALL be present in the resource when using this profile.
 */
@UtilityClass
public class UsCorePatientExtensions {

  /**
   * This Complex Extension for race allows one or more race codes.
   */
  public static final String RACE = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race";
  /**
   * This Complex Extension for ethnicity allows one or more ethnicity codes.
   */
  public static final String ETHNICITY = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity";
}