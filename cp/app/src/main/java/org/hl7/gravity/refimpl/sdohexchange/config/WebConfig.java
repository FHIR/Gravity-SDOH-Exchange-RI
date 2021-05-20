package org.hl7.gravity.refimpl.sdohexchange.config;

import com.healthlx.smartonfhir.config.EnableSmartOnFhir;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Enable Smart-on-FHIR authorization.
@EnableSmartOnFhir
// Enable @Scheduled annotation support needed for a periodic Task polling from CBROs.
@EnableScheduling
@Configuration
public class WebConfig implements WebMvcConfigurer {}
