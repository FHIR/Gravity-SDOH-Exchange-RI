package org.hl7.gravity.refimpl.sdohexchange.config.smartonfhir;

import com.healthlx.smartonfhir.config.EnableSmartOnFhir;
import lombok.RequiredArgsConstructor;
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
@Order(200)
public class SmartOnFhirSecurityConfiguration extends WebSecurityConfigurerAdapter {

  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/administration/$extract")
        .permitAll();
  }
}
