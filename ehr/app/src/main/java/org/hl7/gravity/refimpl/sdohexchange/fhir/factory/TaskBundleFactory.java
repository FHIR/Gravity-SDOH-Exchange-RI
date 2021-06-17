package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import com.google.common.base.Strings;
import lombok.Getter;
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

import java.util.List;

/**
 * Class that holds a logic for creation of a new Task with required referenced resources during a "Create Task" flow.
 * Result is a Transaction Bundle.s
 */
@Getter
@Setter
public class TaskBundleFactory {

  private String name;
  private Patient patient;
  private Coding category;
  private Coding requestCode;
  private Priority priority;
  private OccurrenceRequestDto occurrence;
  private Organization performer;
  private Reference requester;
  private String comment;
  private UserDto user;
  private Consent consent;
  private List<Condition> conditions;
  private List<Goal> goals;

  public Bundle createBundle() {
    Assert.notNull(name, "Name cannot be null.");
    Assert.notNull(patient, "Patient cannot be null.");
    Assert.notNull(category, "SDOH DomainCode cannot be null.");
    Assert.notNull(requestCode, "SDOH RequestCode cannot be null.");
    Assert.notNull(priority, "Priority cannot be null.");
    Assert.notNull(occurrence, "Occurrence cannot be null.");
    Assert.notNull(performer, "Performer (Organization) cannot be null.");
    Assert.notNull(requester, "Requester (Organization) cannot be null.");

    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);

    ServiceRequest serviceRequest = createServiceRequest(consent);
    Task task = createTask(serviceRequest);

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
    serviceRequest.setSubject(getPatientReference());
    conditions.forEach(condition -> serviceRequest.addReasonReference(FhirUtil.toReference(Condition.class,
        condition.getIdElement()
            .getIdPart(), condition.getCode()
            .getCodingFirstRep()
            .getDisplay())));
    goals.forEach(goal -> serviceRequest.addSupportingInfo(FhirUtil.toReference(Goal.class, goal.getIdElement()
        .getIdPart(), goal.getDescription()
        .getCodingFirstRep()
        .getDisplay())));
    serviceRequest.addSupportingInfo(FhirUtil.toReference(Consent.class, consent.getId(), consent.getScope()
        .getCodingFirstRep()
        .getDisplay()));
    return serviceRequest;
  }

  protected Task createTask(ServiceRequest serviceRequest) {
    Task task = new Task();
    task.getMeta()
        .addProfile(SDOHProfiles.TASK);
    task.setStatus(Task.TaskStatus.REQUESTED);
    task.setIntent(Task.TaskIntent.ORDER);
    task.setPriority(priority.getTaskPriority());
    task.setAuthoredOnElement(DateTimeType.now());
    task.setLastModifiedElement(DateTimeType.now());
    TaskCode taskCode = TaskCode.FULFILL;
    task.getCode()
        .addCoding(new Coding(taskCode.getSystem(), taskCode.toCode(), taskCode.getDisplay()));
    task.setDescription(name);
    task.setFocus(new Reference(serviceRequest.getId()));
    task.setFor(getPatientReference());
    task.setOwner(FhirUtil.toReference(Organization.class, performer.getIdElement()
        .getIdPart(), performer.getName()));
    task.setRequester(requester);
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
    consent.setDateTimeElement(DateTimeType.now());
    ConsentScope consentScope = ConsentScope.PATIENTPRIVACY;
    consent.getScope()
        .addCoding(new Coding(consentScope.getSystem(), consentScope.toCode(), consentScope.getDisplay()));
    V3ActCode actCode = V3ActCode.IDSCL;
    consent.addCategory(
        new CodeableConcept().addCoding(new Coding(actCode.getSystem(), actCode.toCode(), actCode.getDisplay())));
    consent.setPatient(getPatientReference());
    ConsentPolicy consentPolicy = ConsentPolicy.HIPAAAUTH;
    consent.getPolicyRule()
        .addCoding(new Coding(consentPolicy.getSystem(), consentPolicy.toCode(), consentPolicy.getDisplay()));
    V3RoleClass roleClass = V3RoleClass.PAT;
    consent.getProvision()
        .addActor()
        .setReference(getPatientReference())
        .getRole()
        .addCoding(new Coding(roleClass.getSystem(), roleClass.toCode(), roleClass.getDisplay()));
    ConsentAction consentAction = ConsentAction.DISCLOSE;
    consent.getProvision()
        .addAction()
        .getCoding()
        .add(new Coding(consentAction.getSystem(), consentAction.toCode(), consentAction.getDisplay()));
    return consent;
  }

  private Reference getPatientReference() {
    return FhirUtil.toReference(Patient.class, patient.getIdElement()
        .getIdPart(), patient.getNameFirstRep()
        .getNameAsSingleString());
  }
}
