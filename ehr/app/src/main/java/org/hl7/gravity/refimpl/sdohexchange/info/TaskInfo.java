package org.hl7.gravity.refimpl.sdohexchange.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mykhailo Stefantsiv
 */
@Getter
@AllArgsConstructor
public class TaskInfo {
  private final Task task;
  private final Map<String, ServiceRequestInfo> serviceRequests;
  private final Map<String, Organization> organizations;
  private final List<Procedure> procedures;
}
