package org.hl7.gravity.refimpl.sdohexchange.config.smartonfhir;

import com.healthlx.smartonfhir.core.SmartOnFhirAccessTokenResponseClient;
import com.healthlx.smartonfhir.core.SmartOnFhirAuthRequestResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Mykhailo Stefantsiv
 */
@Configuration
@EnableWebSecurity
public class SmartOnFhirSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final SmartOnFhirAuthRequestResolver smartOnFhirAuthRequestResolver;
  private final SmartOnFhirAccessTokenResponseClient smartOnFhirAccessTokenResponseClient;

  @Autowired
  SmartOnFhirSecurityConfiguration(SmartOnFhirAuthRequestResolver smartOnFhirAuthRequestResolver,
      SmartOnFhirAccessTokenResponseClient smartOnFhirAccessTokenResponseClient) {
    this.smartOnFhirAuthRequestResolver = smartOnFhirAuthRequestResolver;
    this.smartOnFhirAccessTokenResponseClient = smartOnFhirAccessTokenResponseClient;
  }

  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/administration/$extract")
        .permitAll()
        .anyRequest().authenticated()
        .and().csrf()
        .disable().oauth2Login()
        .authorizationEndpoint()
        .authorizationRequestResolver(this.smartOnFhirAuthRequestResolver)
        .and()
        .tokenEndpoint()
        .accessTokenResponseClient(this.smartOnFhirAccessTokenResponseClient);
  }
}
