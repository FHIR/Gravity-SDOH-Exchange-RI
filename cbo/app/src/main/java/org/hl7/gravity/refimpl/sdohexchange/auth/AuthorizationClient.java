package org.hl7.gravity.refimpl.sdohexchange.auth;

import com.fasterxml.jackson.databind.JsonNode;
import org.hl7.gravity.refimpl.sdohexchange.exception.AuthClientException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * This class can perform OAuth 2 authorization using the Client Credentials flow.
 *
 * @author vladyslav.melnyk
 */
public class AuthorizationClient {

  private static final String GRANT_TYPE = "client_credentials";
  private static final String METADATA_ENDPOINT = "/.well-known/openid-configuration";

  private final RestTemplate restTemplate;

  public AuthorizationClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Extract token endpoint url from the Authorization Server metadata endpoint and return token response after
   * successful authentication.
   *
   * @param authServerUrl Authorization Server Base URL
   * @param clientId      OAuth2 Client ID
   * @param secret        OAuth2 Client Secret
   * @param scope         OAuth2 Client Scope
   * @return {@link TokenResponse} entity
   * @throws AuthClientException will be thrown if token retrieval fails
   */
  public TokenResponse getTokenResponse(URI authServerUrl, String clientId, String secret, String scope)
      throws AuthClientException {
    HttpEntity<MultiValueMap<String, String>> entity = createRequestEntity(clientId, secret, scope);
    String tokenEndpoint = getTokenEndpoint(authServerUrl);
    try {
      return restTemplate.exchange(tokenEndpoint, HttpMethod.POST, entity, TokenResponse.class)
          .getBody();
    } catch (RestClientException e) {
      throw new AuthClientException(e.getMessage(), e);
    }
  }

  /**
   * This method creates request with credentials in headers, but if you want to set credentials in body, you can
   * override this method.
   *
   * @param clientId OAuth2 Client ID
   * @param secret   OAuth2 Client Secret
   * @param scope    OAuth2 Client Scope
   * @return request entity
   */
  protected HttpEntity<MultiValueMap<String, String>> createRequestEntity(String clientId, String secret,
      String scope) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(clientId, secret);
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", GRANT_TYPE);
    map.add("scope", scope);

    return new HttpEntity<>(map, headers);
  }

  private String getTokenEndpoint(URI authServerUrl) {
    String normalizedUrl = URI.create(authServerUrl.toString() + METADATA_ENDPOINT)
        .normalize()
        .toString();
    JsonNode root = restTemplate.getForEntity(normalizedUrl, JsonNode.class)
        .getBody();
    return root.required("token_endpoint")
        .textValue();
  }
}
