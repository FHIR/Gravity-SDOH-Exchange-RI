package org.hl7.gravity.refimpl.sdohexchange.dto.request.characteristic;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public abstract class NewPersonalCharacteristicDto {

  @NotEmpty
  private String type;
  @NotEmpty
  private String method;
  @NotEmpty
  private String value;
}
