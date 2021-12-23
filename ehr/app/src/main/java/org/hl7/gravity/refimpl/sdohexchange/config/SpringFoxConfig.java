package org.hl7.gravity.refimpl.sdohexchange.config;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {

  public static final String USER_API_TAG = "User Controller";
  public static final String CONTEXT_API_TAG = "Context Controller";
  public static final String SUPPORT_API_TAG = "Support Controller";
  public static final String TASK_API_TAG = "Task Controller";
  public static final String PATIENT_TASK_API_TAG = "Patient Task Controller";
  public static final String ASSESSMENT_API_TAG = "Social Risk Assessment Controller";
  public static final String HEALTH_CONCERN_API_TAG = "Health Concern Controller";
  public static final String PROBLEM_API_TAG = "Problem Controller";
  public static final String GOAL_API_TAG = "Goal Controller";
  public static final String MAPPINGS_API_TAG = "Mappings Controller";
  public static final String CONSENT_API_TAG = "Consent Controller";
  public static final String ADMINISTRATION_API_TAG = "Administration Controller";

  @Bean
  public Docket api(ApiInfo apiInfo) {
    return new Docket(DocumentationType.SWAGGER_2).consumes(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
        .produces(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
        .select()
        .apis(RequestHandlerSelectors.basePackage("org.hl7.gravity.refimpl.sdohexchange"))
        .paths(PathSelectors.any())
        .build()
        .tags(new Tag(USER_API_TAG, "Get details of a currently logged in user."),
            new Tag(CONTEXT_API_TAG, "Get context details of a currently logged in user."), new Tag(SUPPORT_API_TAG,
                "Fetch lists of available FHIR resources to reference from Task/ServiceRequest "
                    + "instances being created."), new Tag(TASK_API_TAG,
                "Perform operations on Task resources. This includes creation of tasks in CBRO "
                    + "organizations and triggering an automatic polling mechanism for Task status synchronization."),
            new Tag(PATIENT_TASK_API_TAG,
                "Perform operations on Task resources  to be carried out by the patient or someone acting on their "
                    + "behalf."),
            new Tag(ASSESSMENT_API_TAG, "Get details of past and planned Social Risk Assessments."),
            new Tag(HEALTH_CONCERN_API_TAG, "Get details of active and promoted/resolved Health Concerns."),
            new Tag(PROBLEM_API_TAG, "Get details of active and closed Problems."),
            new Tag(GOAL_API_TAG, "Get details of active and resolved Goals."),
            new Tag(MAPPINGS_API_TAG, "Get details of SDOH categories and codes."), new Tag(ADMINISTRATION_API_TAG,
                "Perform operations and manipulations with FHIR resources, for example "
                    + "converts resources from one to another."),
            new Tag(CONSENT_API_TAG, "Perform operations on Consent resources."))
        .apiInfo(apiInfo)
        .useDefaultResponseMessages(false);
  }

  @Bean
  public ApiInfo apiInfo(@Value("${swagger.title}") String title, Contact contact) {
    return new ApiInfoBuilder().title(title)
        .contact(contact)
        .build();
  }

  @Bean
  public Contact swaggerContact(@Value("${swagger.contact.name}") String name,
      @Value("${swagger.contact.url}") String url) {
    return new Contact(name, url, null);
  }
}