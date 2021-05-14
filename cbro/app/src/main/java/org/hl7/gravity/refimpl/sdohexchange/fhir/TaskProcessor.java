package org.hl7.gravity.refimpl.sdohexchange.fhir;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.fhir.reference.TaskReferenceResourcesLoader;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskProcessor {

  private final TaskValidator taskValidator;
  private final TaskReferenceResourcesLoader taskReferenceResourcesLoader;

  public Bundle process(Task task) {
    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);

    List<String> errors = taskValidator.validate(task);
    if (errors.isEmpty()) {
      log.info("Loading reference resources for Task '" + task.getIdElement()
          .getIdPart() + "'");
      bundle.getEntry()
          .addAll(taskReferenceResourcesLoader.getReferenceResources(task)
              .getEntry());
      log.info("Setting status RECEIVED for Task '" + task.getIdElement()
          .getIdPart() + "'");
      bundle.addEntry(FhirUtil.createPutEntry(task.setStatus(Task.TaskStatus.RECEIVED)
          .setLastModified(new Date())));
    } else {
      log.warn("Setting status REJECTED for Task '" + task.getIdElement()
          .getIdPart() + "'. Reason: " + String.join(";", errors));
      bundle.addEntry(FhirUtil.createPutEntry(task.setStatus(Task.TaskStatus.REJECTED)
          .setStatusReason(new CodeableConcept().setText(String.join("\n", errors)))
          .setLastModified(new Date())));
    }
    return bundle;
  }
}