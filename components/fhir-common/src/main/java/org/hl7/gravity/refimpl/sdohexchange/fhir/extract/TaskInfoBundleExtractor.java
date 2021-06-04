package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor.TaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

public class TaskInfoBundleExtractor extends BundleExtractor<List<TaskInfoHolder>> {

  @Override
  public List<TaskInfoHolder> extract(Bundle bundle) {
    Map<String, ServiceRequest> serviceRequestMap = FhirUtil.getFromBundle(bundle, ServiceRequest.class)
        .stream()
        .collect(Collectors.toMap(serviceRequest -> serviceRequest.getIdElement()
            .getIdPart(), Function.identity()));

    return FhirUtil.getFromBundle(bundle, Task.class)
        .stream()
        .map(task -> {
              ServiceRequest serviceRequest = serviceRequestMap.get(task.getFocus()
                  .getReferenceElement()
                  .getIdPart());
              return new TaskInfoHolder(task, serviceRequest);
            }
        )
        .collect(Collectors.toList());
  }

  @Getter
  public static class TaskInfoHolder {

    private final Task task;
    private final ServiceRequest serviceRequest;

    public TaskInfoHolder(Task task, ServiceRequest serviceRequest) {
      this.task = task;
      this.serviceRequest = serviceRequest;
    }
  }
}