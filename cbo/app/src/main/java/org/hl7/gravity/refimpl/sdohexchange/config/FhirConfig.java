package org.hl7.gravity.refimpl.sdohexchange.config;

import ca.uhn.fhir.context.FhirContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FhirConfig {

  @Bean
  public FhirContext fhirContext() {
    return FhirContext.forR4();
  }
}