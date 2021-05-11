package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import org.hl7.fhir.r4.model.Task;

/**
 * Possible values for {@link org.hl7.fhir.r4.model.Task} status change.
 * @author Mykhailo Stefantsiv
 */
@Getter
public enum TaskStatus {
  CANCELLED(Task.TaskStatus.CANCELLED);

  private Task.TaskStatus value;

  TaskStatus(Task.TaskStatus value) {
    this.value = value;
  }
}
