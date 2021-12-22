package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.annotation.TaskStatusValueMatch;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;

// Copied from cp/app/src/main/java/org/hl7/gravity/refimpl/sdohexchange/dto/request
// Deleted a few fields
@Getter
@Setter
@TaskStatusValueMatch.List({@TaskStatusValueMatch(updateStatus = TaskStatus.REJECTED, requiredFields = {"statusReason"},
    message = "Updating task status to 'Rejected' requires 'reason'."),
    @TaskStatusValueMatch(updateStatus = TaskStatus.CANCELLED, requiredFields = {"statusReason"},
        message = "Updating task status to 'Canceled' requires 'reason'."),
    @TaskStatusValueMatch(updateStatus = TaskStatus.COMPLETED, requiredFields = {"outcome", "procedureCodes"},
        message = "Updating task status to 'Completed' requires 'outcome' and 'procedureCodes''."),
    @TaskStatusValueMatch(updateStatus = TaskStatus.INPROGRESS,
        nullFields = {"procedureCodes", "statusReason", "outcome"},
        message = "Updating task status to 'In Progress' with 'statusReason', 'outcome' or 'procedureCodes' is not "
            + "valid."),
    @TaskStatusValueMatch(updateStatus = TaskStatus.ONHOLD, nullFields = {"procedureCodes", "statusReason", "outcome"},
        message = "Updating task status to 'On Hold' with 'statusReason', 'outcome' or 'procedureCodes' is not valid"
            + ".")})
public class UpdateTaskRequestDto {

  @NotNull
  private Integer serverId;
  private TaskStatus status;
  private String comment;
  private String statusReason;
  private String outcome;
  private List<String> procedureCodes;

  public Task.TaskStatus getTaskStatus() {
    return status == null ? null : status.getTaskStatus();
  }

  public boolean isOnlyAddComment() {
    return (status == null && statusReason == null && outcome == null && procedureCodes == null) && StringUtils.hasText(
        comment);
  }
}
