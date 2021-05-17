package org.hl7.gravity.refimpl.sdohexchange.config;

import com.healthlx.smartonfhir.config.EnableSmartOnFhir;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Enable Smart-on-FHIR authorization.
@EnableSmartOnFhir
// Enable @Scheduled annotation support needed for a periodic Task polling from CBROs.
@EnableScheduling
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    //TODO: restrict CORS only for needed endpoint and needed HTTP methods
    registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("GET", "POST", "OPTIONS", "PUT")
        .allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers")
        .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
        .allowCredentials(true)
        .maxAge(3600);
  }
}
