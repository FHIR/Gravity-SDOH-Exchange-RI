package org.hl7.gravity.refimpl.sdohexchange.info.composer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.info.ServiceRequestInfo;
import org.hl7.gravity.refimpl.sdohexchange.info.TaskInfo;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TasksInfoComposer {

  private final ServiceRequestInfoComposer serviceRequestInfoComposer;

  public List<TaskInfo> compose(Bundle tasksBundle) {
    // Retrieve all Task.focus ServiceRequest instances
    Map<String, ServiceRequest> serviceRequestMap = FhirUtil.getFromBundle(tasksBundle, ServiceRequest.class)
        .stream()
        .collect(Collectors.toMap(serviceRequest -> serviceRequest.getIdElement()
            .getIdPart(), Function.identity()));
    Map<String, ServiceRequestInfo> serviceRequestInfoMap =
        serviceRequestInfoComposer.compose(serviceRequestMap.values());

    // Retrieve all Task.for ServiceRequest instances
    Map<String, Patient> patientMap = FhirUtil.getFromBundle(tasksBundle, Patient.class)
        .stream()
        .collect(Collectors.toMap(patient -> patient.getIdElement()
            .getIdPart(), Function.identity()));

    // Retrieve all Task.requester ServiceRequest instances
    Map<String, Organization> requesterMap = FhirUtil.getFromBundle(tasksBundle, Organization.class)
        .stream()
        .collect(Collectors.toMap(organization -> organization.getIdElement()
            .getIdPart(), Function.identity()));

    return FhirUtil.getFromBundle(tasksBundle, Task.class)
        .stream()
        .map(task -> {
          ServiceRequestInfo serviceRequestInfo = serviceRequestInfoMap.get(task.getFocus()
              .getReferenceElement()
              .getIdPart());

          Patient patient = patientMap.get(task.getFor()
              .getReferenceElement()
              .getIdPart());
          Organization requester = requesterMap.get(task.getRequester()
              .getReferenceElement()
              .getIdPart());
          return new TaskInfo(task, serviceRequestInfo, patient, requester);
        })
        .collect(Collectors.toList());
  }
}
