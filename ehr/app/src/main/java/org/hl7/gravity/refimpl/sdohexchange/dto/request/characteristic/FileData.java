package org.hl7.gravity.refimpl.sdohexchange.dto.request.characteristic;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FileData {

  @NotEmpty
  private String name;
  @NotNull
  private String base64Content;
}
