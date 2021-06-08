package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.annotation.TaskStatusValueMatch;
import org.springframework.util.StringUtils;

@Getter
@Setter
@TaskStatusValueMatch.List({
    @TaskStatusValueMatch(updateStatus = TaskStatus.CANCELLED, requiredFields = {"statusReason"},
        message = "Updating task status to 'Canceled' requires 'reason'.")
})
public class UpdateTaskRequestDto {

  private TaskStatus status;
  private String statusReason;
  private String comment;

  public Task.TaskStatus getStatus() {
    return status == null ? null : status.getTaskStatus();
  }

  public boolean isOnlyAddComment(){
    return (status == null && statusReason == null) && StringUtils.hasText(comment);
  }
}
