package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.annotation.TaskStatusValueMatch;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@TaskStatusValueMatch.List({@TaskStatusValueMatch(updateStatus = Task.TaskStatus.REJECTED, statusField = "status",
    requiredFields = {"outcome"}, message = "Updating task status to 'Rejected' requires outcome."),
    @TaskStatusValueMatch(updateStatus = Task.TaskStatus.COMPLETED, statusField = "status",
        requiredFields = {"outcome", "procedureCodes"},
        message = "Updating task status to 'Completed' requires outcome and Procedure Ids.")})
public class UpdateTaskRequestDto {

  @NonNull
  private TaskStatus status;
  private String comment;
  private String outcome;
  private List<String> procedureCodes;

  public Task.TaskStatus getStatus() {
    return status.getTaskStatus();
  }

  public List<String> getProcedureCodes() {
    return procedureCodes == null ? Collections.emptyList() : procedureCodes;
  }
}