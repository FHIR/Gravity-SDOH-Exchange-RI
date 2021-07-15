package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewHealthConcernDto {

  @NotBlank
  private String name;
  @NotBlank
  private String category;
  @NotBlank
  private String icdCode;
  @NotBlank
  private String snomedCode;
  @NotBlank
  private String basedOnText;
}
