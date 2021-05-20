package org.hl7.gravity.refimpl.sdohexchange.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Configuration
public class FhirConfig {

  @Bean
  public FhirContext fhirContext() {
    return FhirContext.forR4();
  }

  @Bean
  @Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
  public IGenericClient cpClient(OAuth2AuthorizedClientService clientService, FhirContext fhirContext,
      @Value("${cp.fhir-server-uri}") String fhirServerUri) {
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();
    String accessToken = null;
    if (authentication.getClass()
        .isAssignableFrom(OAuth2AuthenticationToken.class)) {
      OAuth2AuthenticationToken oauthToken =
          (OAuth2AuthenticationToken) authentication;
      String clientRegistrationId =
          oauthToken.getAuthorizedClientRegistrationId();
      if (clientRegistrationId.equals("cp-client")) {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
            clientRegistrationId, oauthToken.getName());
        accessToken = client.getAccessToken()
            .getTokenValue();
      }
    }
    IGenericClient fhirClient = fhirContext.newRestfulGenericClient(fhirServerUri);
    fhirClient.registerInterceptor(new BearerTokenAuthInterceptor(accessToken));
    return fhirClient;
  }

  @Bean
  public IGenericClient openCpClient(FhirContext fhirContext,
      @Value("${cp.open-fhir-server-uri}") String fhirServerUri) {
    return fhirContext.newRestfulGenericClient(fhirServerUri);
  }
}