package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.annotation.TaskStatusValueMatch;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@Setter
@TaskStatusValueMatch.List({@TaskStatusValueMatch(updateStatus = TaskStatus.REJECTED, requiredFields = {"statusReason"},
    message = "Updating task status to 'Rejected' requires 'reason'."),
    @TaskStatusValueMatch(updateStatus = TaskStatus.CANCELLED,
        nullFields = {"priorityForCBO", "occurrenceForCBO", "cboPerformer"}, requiredFields = {"statusReason"},
        message = "Updating task status to 'Canceled' requires 'statusReason'. All CBO fields must be unset."),
    @TaskStatusValueMatch(updateStatus = TaskStatus.COMPLETED,
        nullFields = {"priorityForCBO", "occurrenceForCBO", "cboPerformer"},
        requiredFields = {"outcome", "procedureCodes"},
        message = "Updating task status to 'Completed' requires 'outcome' and 'procedureCodes'. All CBO fields must "
            + "be unset."), @TaskStatusValueMatch(updateStatus = TaskStatus.ACCEPTED,
    nullFields = {"procedureCodes", "statusReason", "outcome"}, requiredFields = {"priorityForCBO"},
    message = "Updating task status to 'Accepted' with 'statusReason', 'outcome' or 'procedureCodes' is not "
        + "valid. Required fields is 'priorityForCBO'."), @TaskStatusValueMatch(updateStatus = TaskStatus.INPROGRESS,
    nullFields = {"procedureCodes", "statusReason", "outcome", "priorityForCBO", "occurrenceForCBO", "cboPerformer"},
    message = "Updating task status to 'In Progress' with 'statusReason', 'outcome' or 'procedureCodes' is not "
        + "valid."), @TaskStatusValueMatch(updateStatus = TaskStatus.ONHOLD,
    nullFields = {"procedureCodes", "statusReason", "outcome", "priorityForCBO", "occurrenceForCBO", "cboPerformer"},
    message = "Updating task status to 'On Hold' with 'statusReason', 'outcome' or 'procedureCodes' or any of the CBO "
        + "fields is not valid.")})
public class UpdateTaskRequestDto {

  private TaskStatus status;
  private String comment;
  private String statusReason;
  private String outcome;
  private List<String> procedureCodes;
  // This field is required only when the status is set to ACCEPTED
  private Priority priorityForCBO;
  // This field is required only when the status is set to ACCEPTED
  private OccurrenceRequestDto occurrenceForCBO;
  // This field can be set only when the status is set to ACCEPTED. Not required.
  private String cboPerformer;

  public Task.TaskStatus getTaskStatus() {
    return status == null ? null : status.getTaskStatus();
  }

  public boolean isOnlyAddComment() {
    return (status == null && statusReason == null && outcome == null && procedureCodes == null) && StringUtils.hasText(
        comment);
  }
}