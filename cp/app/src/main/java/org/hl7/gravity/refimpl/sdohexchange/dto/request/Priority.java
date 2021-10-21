package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;

import java.util.stream.Stream;

//TODO copy from EHR app. Refactor.
@AllArgsConstructor
public enum Priority {
  ROUTINE("Routine"),
  URGENT("Urgent"),
  ASAP("ASAP");

  private String priority;

  @JsonCreator
  public static Priority fromText(String priority) {
    return Stream.of(Priority.values())
        .filter(targetEnum -> targetEnum.priority.equals(priority))
        .findFirst()
        .orElse(null);
  }

  @JsonValue
  public String getPriority() {
    return priority;
  }

  public Task.TaskPriority getTaskPriority() {
    return Task.TaskPriority.fromCode(this.priority.toLowerCase());
  }

  public ServiceRequest.ServiceRequestPriority getServiceRequestPriority() {
    return ServiceRequest.ServiceRequestPriority.fromCode(this.priority.toLowerCase());
  }
}