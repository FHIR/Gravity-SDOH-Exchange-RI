package org.hl7.gravity.refimpl.sdohexchange.config;

import org.hl7.gravity.refimpl.sdohexchange.security.oauth2.JwtProfileValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenValidator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;

@Configuration
public class SecurityConfig {

  @Bean
  public JwtDecoderFactory<ClientRegistration> jwtDecoderFactory() {
    OidcIdTokenDecoderFactory decoderFactory = new OidcIdTokenDecoderFactory();
    decoderFactory.setJwtValidatorFactory(
        clientRegistration -> new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator(),
            new OidcIdTokenValidator(clientRegistration), new JwtProfileValidator()));
    return decoderFactory;
  }

}
