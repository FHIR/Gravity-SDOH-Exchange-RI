package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.patienttask;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.OccurrenceRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.Priority;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

/**
 * Class that holds a general logic for creation of a new Patient Task with required referenced resources during a
 * "Create Task" flow. Result is a Transaction Bundle.
 */
@Getter
@Setter
public class PatientTaskBundleFactory {

  private String name;
  private Patient patient;
  private Priority priority;
  private OccurrenceRequestDto occurrence;
  private Reference requester;
  private String comment;
  private UserDto user;

  public Bundle createBundle() {
    Assert.notNull(name, "Name cannot be null.");
    Assert.notNull(patient, "Patient cannot be null.");
    Assert.notNull(priority, "Priority cannot be null.");
    Assert.notNull(occurrence, "Occurrence cannot be null.");
    Assert.notNull(requester, "Requester (Organization) cannot be null.");

    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);

    Task task = createTask();
    bundle.addEntry(FhirUtil.createPostEntry(task));

    return bundle;
  }

  protected Task createTask() {
    Task task = new Task();
    task.getMeta()
        .addProfile(SDOHProfiles.PATIENT_TASK);
    task.setStatus(Task.TaskStatus.READY);
    task.setIntent(Task.TaskIntent.ORDER);
    task.setPriority(priority.getTaskPriority());
    task.setAuthoredOnElement(DateTimeType.now());
    task.setLastModifiedElement(DateTimeType.now());
    task.setDescription(name);
    task.setFor(getPatientReference());
    task.setOwner(getPatientReference());
    if (occurrence.isPeriod()) {
      task.setExecutionPeriod(new Period().setStartElement(occurrence.getStart())
          .setEndElement(occurrence.getEnd()));
    } else {
      task.setExecutionPeriod(new Period().setEndElement(occurrence.getEnd()));
    }
    task.setRequester(requester);

    if (!Strings.isNullOrEmpty(comment)) {
      task.addNote()
          .setText(comment)
          .setTimeElement(DateTimeType.now())
          .setAuthor(new Reference(new IdType(user.getUserType(), user.getId())).setDisplay(user.getName()));
    }
    return task;
  }

  private Reference getPatientReference() {
    return FhirUtil.toReference(Patient.class, patient.getIdElement()
        .getIdPart(), patient.getNameFirstRep()
        .getNameAsSingleString());
  }
}
