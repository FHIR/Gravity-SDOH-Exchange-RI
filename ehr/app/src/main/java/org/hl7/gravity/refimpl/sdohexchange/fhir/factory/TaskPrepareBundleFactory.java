package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import ca.uhn.fhir.rest.api.Constants;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.codesystems.EndpointConnectionType;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCoreProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * Create a transaction bundle to retrieve all necessary resources for Task creation.
 */
@AllArgsConstructor
public class TaskPrepareBundleFactory extends PrepareBundleFactory {

  private final String patient;
  private final String practitioner;
  private final String performer;
  private final String consent;
  private final List<String> conditions;
  private final List<String> goals;

  public Bundle createPrepareBundle() {
    Bundle prepareBundle = new Bundle();
    prepareBundle.setType(BundleType.TRANSACTION);

    prepareBundle.addEntry(getPatientEntry());
    prepareBundle.addEntry(getPractitionerRoleEntry());
    prepareBundle.addEntry(getPerformerWithEndpointEntry());
    prepareBundle.addEntry(getConsentEntry());
    Optional.ofNullable(getConditionsEntry())
        .ifPresent(prepareBundle::addEntry);
    Optional.ofNullable(getGoalsEntry())
        .ifPresent(prepareBundle::addEntry);

    return prepareBundle;
  }

  /**
   * Create GET entry to retrieve a Patient by id.
   *
   * @return patient entry
   */
  protected BundleEntryComponent getPatientEntry() {
    Assert.notNull(patient, "Patient can't be null.");
    return FhirUtil.createGetEntry(path(Patient.class.getSimpleName(), patient));
  }

  /**
   * Create GET entry to retrieve a PractitionerRole by Practitioner id which has US Core profile related to US Core
   * Organization.
   *
   * @return practitioner role entry
   */
  protected BundleEntryComponent getPractitionerRoleEntry() {
    Assert.notNull(practitioner, "Practitioner can't be null.");
    return FhirUtil.createGetEntry(addParams(PractitionerRole.class.getSimpleName(), combineParams(
        eq(PractitionerRole.SP_PRACTITIONER, practitioner),
        eq(Constants.PARAM_PROFILE, UsCoreProfiles.PRACTITIONER_ROLE),
        eq(resourceField(PractitionerRole.SP_ORGANIZATION, Constants.PARAM_PROFILE), UsCoreProfiles.ORGANIZATION)
    )));
  }

  /**
   * Create GET entry to retrieve a Consent by id.
   *
   * @return consent entry
   */
  protected BundleEntryComponent getConsentEntry() {
    Assert.notNull(performer, "Consent can't be null.");
    return FhirUtil.createGetEntry(
        addParams(Consent.class.getSimpleName(), combineParams(eq(Consent.SP_RES_ID, consent))));
  }

  /**
   * Create GET entry to retrieve a performing Organization by id and additionally check whether it has supported type,
   * also include all related Endpoint resources with specific connection type.
   *
   * @return organization and endpoint entry
   */
  protected BundleEntryComponent getPerformerWithEndpointEntry() {
    Assert.notNull(performer, "Performer Organization can't be null.");
    EndpointConnectionType connectionType = EndpointConnectionType.HL7FHIRREST;
    return FhirUtil.createGetEntry(addParams(Organization.class.getSimpleName(), combineParams(
        eq(Organization.SP_RES_ID, performer),
        eq(Organization.SP_TYPE, hasSystemWithAnyCode(OrganizationTypeCode.SYSTEM)),
        eq(Constants.PARAM_INCLUDE, include(Organization.class.getSimpleName(), Organization.SP_ENDPOINT)),
        eq(resourceField(Organization.SP_ENDPOINT, Endpoint.SP_CONNECTION_TYPE),
            hasSystemAndCode(connectionType.getSystem(), connectionType.toCode()))
    )));
  }

  /**
   * Create GET entry to retrieve all Conditions by ids.
   *
   * @return conditions entry
   */
  protected BundleEntryComponent getConditionsEntry() {
    if (conditions == null || conditions.isEmpty()) {
      return null;
    }
    return FhirUtil.createGetEntry(addParams(Condition.class.getSimpleName(), combineParams(
        eq(Condition.SP_RES_ID, String.join(",", conditions)),
        eq(Constants.PARAM_PROFILE, SDOHProfiles.CONDITION)
    )));
  }

  /**
   * Create GET entry to retrieve all Goals by ids.
   *
   * @return goals entry
   */
  protected BundleEntryComponent getGoalsEntry() {
    if (goals == null || goals.isEmpty()) {
      return null;
    }
    return FhirUtil.createGetEntry(addParams(Goal.class.getSimpleName(), combineParams(
        eq(Goal.SP_RES_ID, String.join(",", goals)),
        eq(Constants.PARAM_PROFILE, SDOHProfiles.GOAL)
    )));
  }
}