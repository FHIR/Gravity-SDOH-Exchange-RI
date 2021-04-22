package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.ConsentAction;
import org.hl7.fhir.r4.model.codesystems.ConsentPolicy;
import org.hl7.fhir.r4.model.codesystems.ConsentScope;
import org.hl7.fhir.r4.model.codesystems.TaskCode;
import org.hl7.fhir.r4.model.codesystems.V3ActCode;
import org.hl7.fhir.r4.model.codesystems.V3RoleClass;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.RequestCode;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHDomainCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.Priority;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class that holds a logic for creation of a new Task with required referenced resources during a "Create Task" flow.
 * Result is a Transaction Bundle.s
 */
@Getter
@RequiredArgsConstructor
public class TaskBundleFactory {

  private final String name;
  private final String patientId;
  private final SDOHDomainCode category;
  private final RequestCode request;
  private final Priority priority;
  private final String performerId;
  private final String requesterId;

  @Setter
  private String comment;
  @Setter
  private UserDto user;
  private final List<String> conditionIds = new ArrayList<>();
  private final List<String> goalIds = new ArrayList<>();

  public Bundle createBundle() {
    Assert.notNull(name, "Name cannot be null.");
    Assert.notNull(patientId, "Patient id cannot be null.");
    Assert.notNull(category, "SDOHDomainCode cannot be null.");
    Assert.notNull(request, "RequestCode cannot be null.");
    Assert.notNull(priority, "Priority cannot be null.");
    Assert.notNull(performerId, "Performer (Organization) cannot be null.");
    Assert.notNull(requesterId, "Requester (Organization) cannot be null.");

    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);

    Consent consent = createConsent();
    ServiceRequest serviceRequest = createServiceRequest(consent);
    Task task = createTask(serviceRequest);

    bundle.addEntry(FhirUtil.createPostEntry(consent));
    bundle.addEntry(FhirUtil.createPostEntry(serviceRequest));
    bundle.addEntry(FhirUtil.createPostEntry(task));

    return bundle;
  }

  protected ServiceRequest createServiceRequest(Consent consent) {
    ServiceRequest serviceRequest = new ServiceRequest();
    serviceRequest.getMeta()
        .addProfile(SDOHProfiles.SERVICE_REQUEST);
    serviceRequest.setId(IdType.newRandomUuid());
    serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.ACTIVE);
    serviceRequest.setIntent(ServiceRequest.ServiceRequestIntent.ORDER);
    serviceRequest.setPriority(priority.getServiceRequestPriority());
    //TODO: Implement this. What types to support?
    //    serviceRequest.setOccurrence(value)
    serviceRequest.setAuthoredOnElement(DateTimeType.now());
    // TODO implement validation using InstanceValidator to make sure invalid resources are not created.
    if (!SDOHDomainCategories.requestCodesPerDomain.containsKey(category)) {
      throw new IllegalArgumentException(String.format(
          "Category %s not supported. There are no Request codes available for this value. Supported Categories: [%s]",
          category, SDOHDomainCategories.requestCodesPerDomain.keySet()
              .stream()
              .map(Enum::name)
              .collect(Collectors.joining(", "))));
    }
    if (!SDOHDomainCategories.requestCodesPerDomain.get(category)
        .contains(request)) {
      throw new IllegalArgumentException(
          String.format("Request code %s cannot be used with category %s. Suitable codes are [%s]", request, category,
              SDOHDomainCategories.requestCodesPerDomain.get(category)
                  .stream()
                  .map(Enum::name)
                  .collect(Collectors.joining(", "))));
    }
    serviceRequest.addCategory(
        new CodeableConcept().addCoding(new Coding(category.getSystem(), category.toCode(), category.getDisplay())));
    serviceRequest.getCode()
        .addCoding(new Coding(request.getSystem(), request.toCode(), request.getDisplay()));
    serviceRequest.setSubject(FhirUtil.toReference(Patient.class, patientId));
    conditionIds.forEach(id -> serviceRequest.addReasonReference(FhirUtil.toReference(Condition.class, id)));
    goalIds.forEach(id -> serviceRequest.addSupportingInfo(FhirUtil.toReference(Goal.class, id)));
    Assert.notNull(consent.getId(), "Consent id cannot be null.");
    serviceRequest.addSupportingInfo(new Reference(consent.getIdElement()
        .getValue()));

    //Add description to both Task and ServiceRequest. To be revised.
    if (!Strings.isNullOrEmpty(comment)) {
      serviceRequest.addNote()
          .setText(comment)
          .setTimeElement(DateTimeType.now())
          .setAuthor(new Reference(new IdType(user.getUserType(), user.getId())).setDisplay(user.getName()));
    }
    return serviceRequest;
  }

  protected Task createTask(ServiceRequest serviceRequest) {
    Task task = new Task();
    task.getMeta()
        .addProfile(SDOHProfiles.TASK);
    task.setStatus(Task.TaskStatus.REQUESTED);
    task.setIntent(Task.TaskIntent.ORDER);
    task.setPriority(priority.getTaskPriority());
    DateTimeType now = DateTimeType.now();
    task.setAuthoredOnElement(now);
    task.setLastModified(now.getValue());
    TaskCode taskCode = TaskCode.FULFILL;
    task.getCode()
        .addCoding(new Coding(taskCode.getSystem(), taskCode.toCode(), taskCode.getDisplay()));
    task.setDescription(name);
    Assert.notNull(serviceRequest.getId(), "ServiceRequest id cannot be null.");
    task.setFocus(new Reference(serviceRequest.getIdElement()
        .getValue()));
    task.setFor(FhirUtil.toReference(Patient.class, patientId));
    task.setOwner(FhirUtil.toReference(Organization.class, performerId));
    Assert.notNull(requesterId, "Requester Organization id cannot be null.");
    task.setRequester(FhirUtil.toReference(Organization.class, requesterId));
    //Add description to both Task and ServiceRequest. To be revised.
    if (!Strings.isNullOrEmpty(comment)) {
      task.addNote()
          .setText(comment)
          .setTimeElement(DateTimeType.now())
          .setAuthor(new Reference(new IdType(user.getUserType(), user.getId())).setDisplay(user.getName()));
    }
    return task;
  }

  protected Consent createConsent() {
    Consent consent = new Consent();
    consent.getMeta()
        .addProfile(SDOHProfiles.CONSENT);
    consent.setId(IdType.newRandomUuid());
    consent.setStatus(Consent.ConsentState.ACTIVE);
    consent.setDateTime(new Date());
    ConsentScope consentScope = ConsentScope.PATIENTPRIVACY;
    consent.getScope()
        .addCoding(new Coding(consentScope.getSystem(), consentScope.toCode(), consentScope.getDisplay()));
    V3ActCode actCode = V3ActCode.IDSCL;
    consent.addCategory(
        new CodeableConcept().addCoding(new Coding(actCode.getSystem(), actCode.toCode(), actCode.getDisplay())));
    consent.setPatient(FhirUtil.toReference(Patient.class, patientId));
    consent.setDateTime(new Date());
    ConsentPolicy consentPolicy = ConsentPolicy.HIPAAAUTH;
    consent.getPolicyRule()
        .addCoding(new Coding(consentPolicy.getSystem(), consentPolicy.toCode(), consentPolicy.getDisplay()));
    V3RoleClass roleClass = V3RoleClass.PAT;
    consent.getProvision()
        .addActor()
        .setReference(FhirUtil.toReference(Patient.class, patientId))
        .getRole()
        .addCoding(new Coding(roleClass.getSystem(), roleClass.toCode(), roleClass.getDisplay()));
    ConsentAction consentAction = ConsentAction.DISCLOSE;
    consent.getProvision()
        .addAction()
        .getCoding()
        .add(new Coding(consentAction.getSystem(), consentAction.toCode(), consentAction.getDisplay()));
    return consent;
  }

  /**
   * This class holds mappings between a {@link SDOHDomainCode} and other codes for other resources. Currently only
   * {@link RequestCode} mappings are available. But there are also mappings for Conditions, Procedures, etc. For
   * example, you cannot reference a condition with code 32911000 (Homeless (finding)) to a task from
   * food-insecurity-domain
   */
  private static class SDOHDomainCategories {

    static Map<SDOHDomainCode, Set<RequestCode>> requestCodesPerDomain;

    static {
      requestCodesPerDomain = new HashMap<>();
      requestCodesPerDomain.put(SDOHDomainCode.FOOD_INSECURITY_DOMAIN,
          Sets.newHashSet(RequestCode.ASSESSMENT_OF_HEALTH_AND_SOCIAL_CARE_NEEDS,
              RequestCode.ASSESSMENT_OF_NUTRITIONAL_STATUS, RequestCode.COUNSELING_ABOUT_NUTRITION,
              RequestCode.MEALS_ON_WHEELS_PROVISION_EDUCATION, RequestCode.NUTRITION_EDUCATION,
              RequestCode.PATIENT_REFERRAL_TO_DIETITIAN, RequestCode.PROVISION_OF_FOOD,
              RequestCode.REFERRAL_TO_COMMUNITY_MEALS_SERVICE, RequestCode.REFERRAL_TO_SOCIAL_WORKER));

      requestCodesPerDomain.put(SDOHDomainCode.HOUSING_INSTABILITY_AND_HOMELESSNESS_DOMAIN,
          Sets.newHashSet(RequestCode.HOUSING_ASSESSMENT, RequestCode.REFERRAL_TO_HOUSING_SERVICE));

      requestCodesPerDomain.put(SDOHDomainCode.TRANSPORTATION_INSECURITY_DOMAIN,
          Sets.newHashSet(RequestCode.TRANSPORTATION_CASE_MANAGEMENT));
    }
  }
}
