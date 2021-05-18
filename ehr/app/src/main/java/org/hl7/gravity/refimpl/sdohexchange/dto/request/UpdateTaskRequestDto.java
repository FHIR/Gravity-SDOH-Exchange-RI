package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Task;

/**
 * @author Mykhailo Stefantsiv
 */
@Getter
@Setter
public class UpdateTaskRequestDto {

  private TaskStatus status;
  private String statusReason;
  private String comment;

  public Task.TaskStatus getStatus() {
    return status == null ? null : status.getTaskStatus();
  }
}
