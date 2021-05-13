package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.InvalidTaskStatusException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Getter
@RequiredArgsConstructor
public class TaskUpdateBundleFactory {

  private final Coding RESULTING_ACTIVITY = new Coding(
      "http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes", "resulting-activity",
      "Resulting Activity");

  private final Task task;
  private final Task.TaskStatus status;
  private final String comment;
  private final String outcome;
  private final List<Coding> procedureCodes;

  @Setter
  private UserDto user;
  @Setter
  private ServiceRequest serviceRequest;
  @Setter
  private List<Reference> conditions;

  public Bundle createBundle() {
    Assert.notNull(task, "Task cannot be null.");
    Assert.notNull(status, "Task status cannot be null.");

    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);
    updateTask(bundle);
    bundle.addEntry(FhirUtil.createPutEntry(task));
    return bundle;
  }

  protected void updateTask(Bundle bundle) {
    if (Objects.equals(task.getStatus(), status)) {
      throw new InvalidTaskStatusException();
    }
    task.setStatus(status);
    task.setLastModifiedElement(DateTimeType.now());
    if (StringUtils.hasText(comment)) {
      task.addNote()
          .setText(comment)
          .setTimeElement(DateTimeType.now())
          .setAuthor(new Reference(new IdType(user.getUserType(), user.getId())).setDisplay(user.getName()));
    }
    if (status == Task.TaskStatus.COMPLETED) {
      Assert.notNull(outcome, "Outcome cannot be null.");
      Assert.isTrue(procedureCodes.size() > 0, "Procedures can't be empty.");
      for (Coding code : procedureCodes) {
        Procedure procedure = createProcedure(code);
        bundle.addEntry(FhirUtil.createPostEntry(procedure));

        Task.TaskOutputComponent taskOutput = createTaskOutput(FhirUtil.toReference(Procedure.class,
            procedure.getIdElement()
                .getIdPart(), String.format("%s (%s)", code.getDisplay(), code.getCode())));
        task.getOutput()
            .add(taskOutput);
      }
      task.getOutput()
          .add(createTaskOutput(new CodeableConcept().setText(outcome)));
    } else {
      //TODO: Review this.
      if (StringUtils.hasText(outcome)) {
        task.setStatusReason(new CodeableConcept().setText(outcome));
      }
    }
  }

  protected Procedure createProcedure(Coding coding) {
    Procedure procedure = new Procedure();
    procedure.getMeta()
        .addProfile(SDOHProfiles.PROCEDURE);
    procedure.setId(IdType.newRandomUuid());
    procedure.setStatus(Procedure.ProcedureStatus.COMPLETED);
    procedure.setCategory(serviceRequest.getCategoryFirstRep());
    procedure.getCode()
        .addCoding(coding);
    procedure.getBasedOn()
        .add(task.getFocus());
    procedure.setSubject(task.getFor());
    procedure.setPerformed(DateTimeType.now());
    procedure.setReasonReference(conditions);
    return procedure;
  }

  protected Task.TaskOutputComponent createTaskOutput(Type value) {
    Task.TaskOutputComponent taskOutput = new Task.TaskOutputComponent();
    taskOutput.setType(new CodeableConcept().addCoding(RESULTING_ACTIVITY));
    taskOutput.setValue(value);
    return taskOutput;
  }
}