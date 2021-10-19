package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class NewServerDto {

  @NotBlank
  @Size(max = 64)
  private String serverName;

  @NotNull
  @URL(regexp = "^(http|https).*")
  private String fhirServerUrl;

  @NotNull
  @URL(regexp = "^(http|https).*")
  private String authServerUrl;

  @NotBlank
  private String clientId;

  @NotBlank
  private String clientSecret;
}
