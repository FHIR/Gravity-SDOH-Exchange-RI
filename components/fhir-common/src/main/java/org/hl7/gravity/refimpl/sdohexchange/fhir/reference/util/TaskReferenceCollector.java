package org.hl7.gravity.refimpl.sdohexchange.fhir.reference.util;

import lombok.experimental.UtilityClass;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskOutputComponent;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class TaskReferenceCollector {

  public static List<Reference> getOutputProcedures(Task task) {
    return Optional.ofNullable(task)
        .map(Task::getOutput)
        .orElse(Collections.emptyList())
        .stream()
        .map(TaskOutputComponent::getValue)
        .filter(value -> value instanceof Reference)
        .map(Reference.class::cast)
        .filter(reference -> reference.getReferenceElement()
            .getResourceType()
            .equals(Procedure.class.getSimpleName()))
        .collect(Collectors.toList());
  }
}