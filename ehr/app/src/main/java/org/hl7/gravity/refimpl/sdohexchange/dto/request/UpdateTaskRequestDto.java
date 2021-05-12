package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mykhailo Stefantsiv
 */
@Getter
@Setter
public class UpdateTaskRequestDto {
  private TaskStatus status;
  private String comment;
}
