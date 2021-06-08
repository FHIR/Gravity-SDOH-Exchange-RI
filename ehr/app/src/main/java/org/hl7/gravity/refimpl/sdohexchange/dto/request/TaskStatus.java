package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.r4.model.Task;

import java.util.stream.Stream;

@AllArgsConstructor
public enum TaskStatus {
  CANCELLED(Task.TaskStatus.CANCELLED.getDisplay(), Task.TaskStatus.CANCELLED);

  private final String status;
  @Getter
  private final Task.TaskStatus taskStatus;

  @JsonCreator
  public static TaskStatus fromText(String status) {
    return Stream.of(TaskStatus.values())
        .filter(targetEnum -> targetEnum.status.equals(status))
        .findFirst()
        .orElseThrow(IllegalAccessError::new);
  }

  @JsonValue
  public String getStatus() {
    return status;
  }
}
