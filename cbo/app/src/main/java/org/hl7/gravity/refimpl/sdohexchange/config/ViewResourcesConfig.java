package org.hl7.gravity.refimpl.sdohexchange.config;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.gravity.refimpl.sdohexchange.fhir.ResourceLoader;
import org.hl7.gravity.refimpl.sdohexchange.fhir.ResourceParser;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ViewResourcesConfig {

  @Bean
  public ResourceParser resourceParser(FhirContext fhirContext) {
    return new ResourceParser(fhirContext);
  }

  @Bean
  public ResourceLoader resourceLoader() {
    return new ResourceLoader();
  }
}
