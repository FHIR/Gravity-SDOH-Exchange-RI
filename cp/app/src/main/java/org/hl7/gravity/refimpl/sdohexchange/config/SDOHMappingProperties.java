package org.hl7.gravity.refimpl.sdohexchange.config;

import org.hl7.gravity.refimpl.sdohexchange.sdohmappings.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.io.support.YmlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "sdoh-mappings.yml", factory = YmlPropertySourceFactory.class)
public class SDOHMappingProperties {

  @Bean
  @ConfigurationProperties("sdoh-mappings")
  public SDOHMappings sdohMappings() {
    return new SDOHMappings();
  }
}