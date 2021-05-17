package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.annotation.TaskStatusValueMatch;

@Getter
@Setter
@TaskStatusValueMatch.List({@TaskStatusValueMatch(updateStatus = Task.TaskStatus.REJECTED, statusField = "status",
    requiredFields = {"statusReason"}, message = "Updating task status to 'Rejected' requires reason."),
    @TaskStatusValueMatch(updateStatus = Task.TaskStatus.CANCELLED, statusField = "status",
        requiredFields = {"statusReason"}, message = "Updating task status to 'Canceled' requires reason."),
    @TaskStatusValueMatch(updateStatus = Task.TaskStatus.COMPLETED, statusField = "status",
        requiredFields = {"outcome", "procedureCodes"},
        message = "Updating task status to 'Completed' requires outcome and Procedure Ids.")})
public class UpdateTaskRequestDto {

  @NonNull
  private TaskStatus status;
  private String comment;
  private String statusReason;
  private String outcome;
  private List<String> procedureCodes;

  public Task.TaskStatus getStatus() {
    return status.getTaskStatus();
  }

  public List<String> getProcedureCodes() {
    return procedureCodes == null ? Collections.emptyList() : procedureCodes;
  }
}