package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Mykhailo Stefantsiv
 */
@Getter
@Setter
@AllArgsConstructor
public class EmailDto {

  private String use;
  private String email;
}
