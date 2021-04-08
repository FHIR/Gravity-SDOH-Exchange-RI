package org.hl7.gravity.refimpl.sdohexchange.fhir;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.codesystems.TaskCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskValidator {

  public List<String> validate(Task task) {
    List<String> errors = new ArrayList<>();
    //TODO: Check whether identifier system exists in CBRO Organization resource
    if (task.getIdentifier()
        .size() != 1) {
      errors.add("Task.identifier is not valid.");
    }
    if (!task.getIntent()
        .equals(Task.TaskIntent.ORDER)) {
      errors.add(
          String.format("Task.intent has wrong value. Supported value is '%s'.", Task.TaskIntent.ORDER.toCode()));
    }
    TaskCode taskCode = TaskCode.FULFILL;
    if (task.getCode()
        .getCoding()
        .stream()
        .noneMatch(coding -> taskCode.getSystem()
            .equals(coding.getSystem()) && taskCode.toCode()
            .equals(coding.getCode()) && taskCode.getDisplay()
            .equals(coding.getDisplay()))) {
      errors.add(String.format("Task.code has no coding with system '%s' and code '%s'.", taskCode.getSystem(),
          taskCode.toCode()));
    }
    if (!task.hasFocus()) {
      errors.add("Task.focus is missing.");
    }
    if (!task.hasFor()) {
      errors.add("Task.for is missing.");
    }
    if (task.hasFor() && !task.getFor()
        .getReferenceElement()
        .getResourceType()
        .equals(Patient.class.getSimpleName())) {
      errors.add(
          String.format("Task.for reference is invalid. Supported reference is '%s'.", Patient.class.getSimpleName()));
    }
    if (!task.hasOwner()) {
      errors.add("Task.owner is missing.");
    }
    if (task.hasOwner() && !task.getOwner()
        .getReferenceElement()
        .getResourceType()
        .equals(Organization.class.getSimpleName())) {
      errors.add(String.format("Task.owner reference is invalid. Supported reference is '%s'.",
          Organization.class.getSimpleName()));
    }
    return errors;
  }
}
