package org.hl7.gravity.refimpl.sdohexchange.fhir.reference.util;

import lombok.experimental.UtilityClass;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Task;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Copied from cp/app/src/main/java/org/hl7/gravity/refimpl/sdohexchange/fhir/reference/util
@UtilityClass
public class TaskReferenceCollector {

  public static List<Reference> getOutputProcedures(Task task) {
    return Optional.ofNullable(task)
        .map(Task::getOutput)
        .orElse(Collections.emptyList())
        .stream()
        .map(Task.TaskOutputComponent::getValue)
        .filter(value -> value instanceof Reference)
        .map(Reference.class::cast)
        .filter(reference -> reference.getReferenceElement()
            .getResourceType()
            .equals(Procedure.class.getSimpleName()))
        .collect(Collectors.toList());
  }
}
