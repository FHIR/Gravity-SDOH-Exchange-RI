package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import com.google.common.base.Strings;
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
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.ConsentAction;
import org.hl7.fhir.r4.model.codesystems.ConsentPolicy;
import org.hl7.fhir.r4.model.codesystems.ConsentScope;
import org.hl7.fhir.r4.model.codesystems.TaskCode;
import org.hl7.fhir.r4.model.codesystems.V3ActCode;
import org.hl7.fhir.r4.model.codesystems.V3RoleClass;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.OccurrenceRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.Priority;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class that holds a logic for creation of a new Task with required referenced resources during a "Create Task" flow.
 * Result is a Transaction Bundle.s
 */
@Getter
@RequiredArgsConstructor
public class TaskBundleFactory {

  private final String name;
  private final String patientId;
  private final Coding category;
  private final Coding requestCode;
  private final Priority priority;
  private final OccurrenceRequestDto occurrence;
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
    Assert.notNull(requestCode, "RequestCode cannot be null.");
    Assert.notNull(priority, "Priority cannot be null.");
    Assert.notNull(occurrence, "Occurrence cannot be null.");
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
    serviceRequest.setAuthoredOnElement(DateTimeType.now());
    if (occurrence.isPeriod()) {
      serviceRequest.setOccurrence(new Period().setStartElement(occurrence.getStart())
          .setEndElement(occurrence.getEnd()));
    } else {
      serviceRequest.setOccurrence(occurrence.getEnd());
    }
    serviceRequest.addCategory(new CodeableConcept().addCoding(category));
    serviceRequest.getCode()
        .addCoding(requestCode);
    serviceRequest.setSubject(FhirUtil.toReference(Patient.class, patientId));
    conditionIds.forEach(id -> serviceRequest.addReasonReference(FhirUtil.toReference(Condition.class, id)));
    goalIds.forEach(id -> serviceRequest.addSupportingInfo(FhirUtil.toReference(Goal.class, id)));
    Assert.notNull(consent.getId(), "Consent id cannot be null.");
    serviceRequest.addSupportingInfo(new Reference(consent.getIdElement()
        .getValue()));
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
}
