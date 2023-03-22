package org.hl7.gravity.refimpl.sdohexchange.config.smartonfhir;

import com.healthlx.smartonfhir.config.EnableSmartOnFhir;
import com.healthlx.smartonfhir.core.Oauth2TokenResponseAwareAuthenticationSuccessHandler;
import com.healthlx.smartonfhir.core.SmartOnFhirAccessTokenResponseClient;
import com.healthlx.smartonfhir.core.SmartOnFhirAuthRequestResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Mykhailo Stefantsiv
 */
@Configuration
@RequiredArgsConstructor
@EnableSmartOnFhir
@Order(50)
public class SmartOnFhirSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private SmartOnFhirAuthRequestResolver smartOnFhirAuthRequestResolver;
  @Autowired
  private SmartOnFhirAccessTokenResponseClient smartOnFhirAccessTokenResponseClient;
  @Autowired
  private Oauth2TokenResponseAwareAuthenticationSuccessHandler authenticationSuccessHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // TODO: Enable when SMART on FHIR is ready
    // http.authorizeRequests()
    // .antMatchers("/administration/$extract")
    // .permitAll()
    // .anyRequest()
    // .authenticated()
    // .and()
    // .csrf()
    // .disable()
    // .oauth2Login()
    // .successHandler(authenticationSuccessHandler)
    // .authorizationEndpoint()
    // .authorizationRequestResolver(this.smartOnFhirAuthRequestResolver)
    // .and()
    // .tokenEndpoint()
    // .accessTokenResponseClient(this.smartOnFhirAccessTokenResponseClient);
  }
}
