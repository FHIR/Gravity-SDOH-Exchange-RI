package org.hl7.gravity.refimpl.sdohexchange.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

@Component
public class IGenericClientProvider {

  private OAuth2AuthorizedClientService authorizedClientService;

  private final FhirContext fhirContext;
  private final String fhirServerUri;

  @Autowired
  public IGenericClientProvider(OAuth2AuthorizedClientService authorizedClientService, FhirContext fhirContext,
      @Value("${ehr.fhir-server-uri}") String fhirServerUri) {
    this.authorizedClientService = authorizedClientService;
    this.fhirContext = fhirContext;
    this.fhirServerUri = fhirServerUri;
  }

  public IGenericClient client() {
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();
    OAuth2AuthorizedClient authorizedClient = this.authorizedClientService.loadAuthorizedClient("ehr-client",
        authentication.getName());

    OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

    return client(fhirServerUri, accessToken.getTokenValue());
  }

  public IGenericClient client(String fhiServerUri, String accessToken) {
    IGenericClient client = fhirContext.newRestfulGenericClient(fhiServerUri);
    if (accessToken != null) {
      IClientInterceptor is = new BearerTokenAuthInterceptor(accessToken);
      client.registerInterceptor(is);
    }
    return client;
  }

}