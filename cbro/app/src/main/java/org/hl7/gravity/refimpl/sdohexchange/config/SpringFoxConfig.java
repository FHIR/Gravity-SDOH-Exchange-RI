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
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {

  @Bean
  public Docket api(ApiInfo apiInfo) {
    return new Docket(DocumentationType.SWAGGER_2).consumes(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
        .produces(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
        .select()
        .apis(RequestHandlerSelectors.basePackage("org.hl7.gravity.refimpl.sdohexchange"))
        .paths(PathSelectors.any())
        .build()
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