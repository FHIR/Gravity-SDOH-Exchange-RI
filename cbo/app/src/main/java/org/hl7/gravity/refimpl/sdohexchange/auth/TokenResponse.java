package org.hl7.gravity.refimpl.sdohexchange.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {

  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("scope")
  private String scope;
  @JsonProperty("token_type")
  private String tokenType;
  @JsonProperty("expires_in")
  private long expiresIn;
}
