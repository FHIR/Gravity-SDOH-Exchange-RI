package org.hl7.gravity.refimpl.sdohexchange.fhir.reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.gravity.refimpl.sdohexchange.fhir.reference.util.ServiceRequestReferenceCollector;

/**
 * Reference resolver for {@link ServiceRequest} resource.
 */
public class ServiceRequestReferenceResolver implements ReferenceResolver {

  private final String identifierSystem;

  @Getter
  private List<Reference> consentsRef;
  @Getter
  private List<Reference> conditionsRefs;
  @Getter
  private List<Reference> goalsRefs;

  private List<Reference> externalConsentRefs;
  private List<Reference> externalConditionRefs;
  private List<Reference> externalGoalRefs;

  private Map<String, Consent> localConsents;
  private Map<String, Condition> localConditions;
  private Map<String, Goal> localGoals;

  private Map<String, Consent> externalConsents;
  private Map<String, Condition> externalConditions;
  private Map<String, Goal> externalGoals;

  public ServiceRequestReferenceResolver(ServiceRequest serviceRequest, String identifierSystem) {
    this.consentsRef = ServiceRequestReferenceCollector.getConsents(serviceRequest);
    this.conditionsRefs = ServiceRequestReferenceCollector.getConditions(serviceRequest);
    this.goalsRefs = ServiceRequestReferenceCollector.getGoals(serviceRequest);
    this.identifierSystem = identifierSystem;
  }

  @Override
  public List<Reference> getLocalReferences() {
    ArrayList<Reference> localReferences = new ArrayList<>();
    localReferences.addAll(consentsRef);
    localReferences.addAll(conditionsRefs);
    localReferences.addAll(goalsRefs);
    return localReferences;
  }

  @Override
  public void setLocalResources(Map<Class<? extends Resource>, List<Resource>> localResources) {
    this.localConsents = collectResources(localResources, Consent.class, c -> c.getIdentifierFirstRep()
        .getValue());
    this.localConditions = collectResources(localResources, Condition.class, c -> c.getIdentifierFirstRep()
        .getValue());
    this.localGoals = collectResources(localResources, Goal.class, g -> g.getIdentifierFirstRep()
        .getValue());

    this.externalConsentRefs = consentsRef.stream()
        .filter(ref -> !localConsents.containsKey(ref.getReferenceElement()
            .getIdPart()))
        .collect(Collectors.toList());
    this.externalConditionRefs = conditionsRefs.stream()
        .filter(ref -> !localConditions.containsKey(ref.getReferenceElement()
            .getIdPart()))
        .collect(Collectors.toList());
    this.externalGoalRefs = goalsRefs.stream()
        .filter(ref -> !localGoals.containsKey(ref.getReferenceElement()
            .getIdPart()))
        .collect(Collectors.toList());
  }

  @Override
  public void setExternalResources(Map<Class<? extends Resource>, List<Resource>> externalResources) {
    this.externalConsents = collectResources(externalResources, Consent.class, c -> c.getIdElement()
        .getIdPart());
    this.externalConditions = collectResources(externalResources, Condition.class, c -> c.getIdElement()
        .getIdPart());
    this.externalGoals = collectResources(externalResources, Goal.class, g -> g.getIdElement()
        .getIdPart());
  }

  @Override
  public List<Reference> getExternalReferences() {
    ArrayList<Reference> externalReferences = new ArrayList<>();
    externalReferences.addAll(externalConsentRefs);
    externalReferences.addAll(externalConditionRefs);
    externalReferences.addAll(externalGoalRefs);
    return externalReferences;
  }

  public Condition getCondition(IIdType iIdType) {
    Condition condition = localConditions.get(iIdType.getIdPart());
    if (condition == null) {
      condition = externalConditions.get(iIdType.getIdPart())
          .copy();
      // Remove SDOH profile, Logica does not support this.
      // TODO Use SDOH Profiles.
      condition.setMeta(null);
      // Set identifier to link resource from EHR
      condition.addIdentifier()
          .setSystem(identifierSystem)
          .setValue(condition.getIdElement()
              .getIdPart());
      condition.setId(IdType.newRandomUuid());
    }
    return condition;
  }

  public boolean createCondition(IIdType iIdType) {
    return externalConditions.containsKey(iIdType.getIdPart());
  }

  public Consent getReferenceConsent(IIdType iIdType) {
    return externalConsents.get(iIdType.getIdPart());
  }

  public Goal getGoal(IIdType iIdType) {
    Goal goal = localGoals.get(iIdType.getIdPart());
    if (goal == null) {
      goal = externalGoals.get(iIdType.getIdPart())
          .copy();
      // Remove SDOH profile, Logica does not support this.
      // TODO Use SDOH Profiles.
      goal.setMeta(null);
      // Set identifier to link resource from EHR
      goal.addIdentifier()
          .setSystem(identifierSystem)
          .setValue(goal.getIdElement()
              .getIdPart());
      goal.setId(IdType.newRandomUuid());
    }
    return goal;
  }

  public boolean createGoal(IIdType iIdType) {
    return externalGoals.containsKey(iIdType.getIdPart());
  }

  public Consent getConsent(IIdType iIdType) {
    Consent consent = externalConsents.get(iIdType.getIdPart())
        .copy();
    // Remove SDOH profile, Logica does not support this.
    // TODO Use SDOH Profiles.
    consent.setMeta(null);
    // Set identifier to link resource from EHR
    consent.addIdentifier()
        .setSystem(identifierSystem)
        .setValue(consent.getIdElement()
            .getIdPart());
    consent.setId(IdType.newRandomUuid());
    return consent;
  }

  public boolean createConsent(IIdType iIdType) {
    return externalConsents.containsKey(iIdType.getIdPart());
  }

  private <T extends Resource> Map<String, T> collectResources(Map<Class<? extends Resource>, List<Resource>> resources,
      Class<T> key, Function<? super T, String> keyMapper) {
    return Optional.ofNullable(resources.get(key))
        .orElse(Collections.emptyList())
        .stream()
        .filter(key::isInstance)
        .map(key::cast)
        .collect(Collectors.toMap(keyMapper, Function.identity()));
  }
}
