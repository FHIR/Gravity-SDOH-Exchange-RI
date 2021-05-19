package org.hl7.gravity.refimpl.sdohexchange.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Enable @Scheduled annotation support needed for a periodic Task polling from CBROs.
@EnableScheduling
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/administration/$extract")
        .allowedOrigins("*")
        .allowedMethods("POST", "OPTIONS")
        .allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers")
        .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
        .allowCredentials(true)
        .maxAge(3600);
  }
}
