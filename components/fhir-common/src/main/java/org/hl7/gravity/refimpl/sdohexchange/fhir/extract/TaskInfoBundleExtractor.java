package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor.TaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.List;
import java.util.stream.Collectors;

public class TaskInfoBundleExtractor extends BundleExtractor<List<TaskInfoHolder>> {

  @Override
  public List<TaskInfoHolder> extract(Bundle bundle) {
    return FhirUtil.getFromBundle(bundle, Task.class)
        .stream()
        .map(task -> {
          if (!(task.getFocus()
              .getResource() instanceof ServiceRequest)) {
            String reason = String.format("Task resource with id '%s' does not contain focus of type ServiceRequest.",
                task.getIdElement()
                    .getIdPart());
            throw new TaskInfoBundleExtractorException(reason);
          }
          ServiceRequest serviceRequest = (ServiceRequest) task.getFocus()
              .getResource();
          return new TaskInfoHolder(task, serviceRequest);
        })
        .collect(Collectors.toList());
  }

  @Getter
  @RequiredArgsConstructor
  public static class TaskInfoHolder {

    private final Task task;
    private final ServiceRequest serviceRequest;
  }

  public static class TaskInfoBundleExtractorException extends RuntimeException {

    public TaskInfoBundleExtractorException(String message) {
      super(message);
    }
  }
}