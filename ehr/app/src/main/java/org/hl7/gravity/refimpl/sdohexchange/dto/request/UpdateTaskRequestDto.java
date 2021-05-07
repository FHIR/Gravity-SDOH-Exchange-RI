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
  private String id;
  private Task.TaskStatus status;
  //TODO: support comments
}
