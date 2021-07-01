package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import com.google.common.base.Strings;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.codesystems.EndpointConnectionType;
import org.hl7.gravity.refimpl.sdohexchange.exception.TaskPrepareException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskPrepareBundleExtractor.TaskPrepareInfoHolder;

/**
 * Transaction bundle parser of resources required for Task creation.
 */
public class TaskPrepareBundleExtractor extends BundleExtractor<TaskPrepareInfoHolder> {

  @Override
  public TaskPrepareInfoHolder extract(Bundle bundle) {
    return new TaskPrepareInfoHolder(extractToMap(bundle));
  }

  @Getter
  public class TaskPrepareInfoHolder {

    private final Patient patient;
    private final PractitionerRole practitionerRole;
    private final Organization performerOrganization;
    private final Endpoint endpoint;
    private final Consent consent;
    private final List<Condition> conditions;
    private final List<Goal> goals;

    public TaskPrepareInfoHolder(Map<? extends Class<? extends Resource>, List<Resource>> resources) {
      this.patient = resourceList(resources, Patient.class)
          .stream()
          .findFirst()
          .orElseThrow(() -> new TaskPrepareException("Patient not found."));
      this.practitionerRole = getRole(resourceList(resources, PractitionerRole.class));
      this.performerOrganization = resourceList(resources, Organization.class)
          .stream()
          .findFirst()
          .orElseThrow(() -> new TaskPrepareException("Performer Organization not found."));
      this.endpoint = getEndpoint(resourceList(resources, Endpoint.class));
      this.conditions = resourceList(resources, Condition.class);
      this.goals = resourceList(resources, Goal.class);
      this.consent = getConsent(resourceList(resources, Consent.class));
    }

    public Reference getPerformer() {
      return getPractitionerRole().getOrganization();
    }

    public List<Condition> getConditions(List<String> conditionsIds) {
      if (conditionsIds != null && conditionsIds.size() != getConditions().size()) {
        throw new TaskPrepareException("Conditions don't exist or are not supported.");
      }
      return getConditions();
    }

    public List<Goal> getGoals(List<String> goalIds) {
      if (goalIds != null && goalIds.size() != getGoals().size()) {
        throw new TaskPrepareException("Goals don't exist or are not supported.");
      }
      return getGoals();
    }

    private PractitionerRole getRole(List<PractitionerRole> roles) {
      if (roles.isEmpty()) {
        throw new TaskPrepareException(
            "No Practitioner role with US Core profile which references to US Core Organization have been found.");
      } else if (roles.size() > 1) {
        throw new TaskPrepareException(
            "More than one Practitioner role with US Core profile which references to US Core Organization have been "
                + "found.");
      }
      return roles.stream()
          .findFirst()
          .get();
    }

    private Endpoint getEndpoint(List<Endpoint> endpoints) {
      String organizationId = performerOrganization.getIdElement()
          .getIdPart();
      Endpoint endpoint = endpoints.stream()
          .findFirst()
          .map(Endpoint.class::cast)
          .orElseThrow(() -> new TaskPrepareException(
              String.format("CP Organization resource with id '%s' does not contain endpoint of type '%s'.",
                  organizationId, EndpointConnectionType.HL7FHIRREST)));
      if (Strings.isNullOrEmpty(endpoint.getAddress())) {
        throw new TaskPrepareException(
            String.format("Endpoint resource with id '%s' for a CP organization '%s' does not contain an address.",
                endpoint.getIdElement()
                    .getIdPart(), organizationId));
      }
      return endpoint;
    }

    private Consent getConsent(List<Consent> consents) {
      if (consents.isEmpty()) {
        throw new TaskPrepareException("No Consent have been found.");
      }
      return consents.stream()
          .findFirst()
          .get();
    }
  }
}