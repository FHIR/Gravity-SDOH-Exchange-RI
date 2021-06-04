package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hl7.fhir.r4.model.Task;

import java.util.stream.Stream;
import org.hl7.gravity.refimpl.sdohexchange.exception.TaskUpdateException;

@AllArgsConstructor
public enum TaskStatus {
  ACCEPTED(Task.TaskStatus.ACCEPTED.getDisplay(), Task.TaskStatus.ACCEPTED),
  REJECTED(Task.TaskStatus.REJECTED.getDisplay(), Task.TaskStatus.REJECTED),
  CANCELLED(Task.TaskStatus.CANCELLED.getDisplay(), Task.TaskStatus.CANCELLED),
  ONHOLD(Task.TaskStatus.ONHOLD.getDisplay(), Task.TaskStatus.ONHOLD),
  INPROGRESS(Task.TaskStatus.INPROGRESS.getDisplay(), Task.TaskStatus.INPROGRESS),
  COMPLETED(Task.TaskStatus.COMPLETED.getDisplay(), Task.TaskStatus.COMPLETED);

  private final String status;
  @Getter
  private final Task.TaskStatus taskStatus;

  @JsonCreator
  public static TaskStatus fromText(String status) {
    return Stream.of(TaskStatus.values())
        .filter(targetEnum -> targetEnum.status.equals(status))
        .findFirst()
        .orElseThrow(() -> new TaskUpdateException(String.format("Invalid Task status '%s'", status)));
  }

  @JsonValue
  public String getStatus() {
    return status;
  }
}