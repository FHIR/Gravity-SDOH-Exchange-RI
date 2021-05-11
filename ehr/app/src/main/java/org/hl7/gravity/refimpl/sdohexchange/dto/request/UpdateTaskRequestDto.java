package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author Mykhailo Stefantsiv
 */
@Getter
@Setter
public class UpdateTaskRequestDto {
  @NotNull
  private TaskStatus status;
  private String comment;
}
