package org.hl7.gravity.refimpl.sdohexchange.security.oauth2;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;

public class JwtProfileValidator implements OAuth2TokenValidator<Jwt> {

  private final OAuth2Error INVALID_PROFILE = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
      "The ID Token does not contain required 'profile' claim.", "https://tools.ietf.org/html/rfc6750#section-3.1");

  @Override
  public OAuth2TokenValidatorResult validate(Jwt jwt) {
    Assert.notNull(jwt, "jwt cannot be null");

    String profile = jwt.getClaimAsString("profile");
    if (profile == null) {
      return OAuth2TokenValidatorResult.failure(INVALID_PROFILE);
    } else {
      return OAuth2TokenValidatorResult.success();
    }
  }
}