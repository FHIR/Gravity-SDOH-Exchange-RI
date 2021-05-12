package org.hl7.gravity.refimpl.sdohexchange.info.composer;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.ProcedureRepository;
import org.hl7.gravity.refimpl.sdohexchange.info.ServiceRequestInfo;
import org.hl7.gravity.refimpl.sdohexchange.info.TaskInfo;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Composes list of {@link TaskInfo} objects, which contains all info (all needed resources retrieved) for each
 * particular {@link Task}.
 *
 * @author Mykhailo Stefantsiv
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TasksInfoComposer {

  private final ProcedureRepository procedureRepository;
  private final ServiceRequestInfoComposer serviceRequestInfoComposer;

  public List<TaskInfo> compose(Bundle tasksBundle) {
    // Retrieve all Task.focus ServiceRequest instances
    Map<String, ServiceRequestInfo> srMap = FhirUtil.getFromBundle(tasksBundle, ServiceRequest.class)
        .stream()
        .map(serviceRequestInfoComposer::compose)
        .collect(Collectors.toMap(r -> r.getServiceRequest()
            .getIdElement()
            .getIdPart(), Function.identity()));

    // Retrieve all Task.owner Organization instances
    Map<String, Organization> orgMap = FhirUtil.getFromBundle(tasksBundle, Organization.class)
        .stream()
        .collect(Collectors.toMap(r -> r.getIdElement()
            .getIdPart(), Function.identity()));

    return FhirUtil.getFromBundle(tasksBundle, Task.class)
        .stream()
        .map(t -> new TaskInfo(t, srMap, orgMap, composeProceduresList(t)))
        .collect(Collectors.toList());
  }

  private List<Procedure> composeProceduresList(Task task) {
    List<Procedure> procedures = new ArrayList<>();
    List<String> procedureIds = new ArrayList<>();
    for (Task.TaskOutputComponent outputComponent : task.getOutput()) {
      Reference procedureReference = (Reference) outputComponent.getValue();
      if (procedureReference != null) {
        procedureIds.add(procedureReference.getReferenceElement()
            .getIdPart());
      }
    }
    if (!procedureIds.isEmpty()) {
      Bundle bundle = procedureRepository.find(procedureIds);
      procedures.addAll(FhirUtil.getFromBundle(bundle, Procedure.class));
    }
    return procedures;
  }
}
