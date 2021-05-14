package org.hl7.gravity.refimpl.sdohexchange.info;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Task;

/**
 * @author Mykhailo Stefantsiv
 */
@Getter
@AllArgsConstructor
public class TaskInfo {

  private final Task task;
  private final Map<String, ServiceRequestInfo> serviceRequests;
  private final Map<String, Organization> organizations;
}
