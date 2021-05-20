package org.hl7.gravity.refimpl.sdohexchange;

import java.nio.charset.StandardCharsets;
import javax.validation.ConstraintViolationException;
import org.hl7.gravity.refimpl.sdohexchange.controller.RestExceptionHandlerController;
import org.hl7.gravity.refimpl.sdohexchange.handlers.ConstraintViolationExceptionHandler;
import org.hl7.gravity.refimpl.sdohexchange.handlers.MessageSourceExceptionHandler;
import org.hl7.gravity.refimpl.sdohexchange.handlers.MethodArgumentNotValidExceptionHandler;
import org.hl7.gravity.refimpl.sdohexchange.handlers.RestExceptionHandler;
import org.hl7.gravity.refimpl.sdohexchange.interpolators.SpelMessageInterpolator;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * REST exception handlers auto bean configuration.
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(RestExceptionHandler.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandlerAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public RestExceptionHandler<Exception> restExceptionHandler(MessageSource messageSource,
      SpelMessageInterpolator spelMessageInterpolator) {
    return new MessageSourceExceptionHandler<>(messageSource, spelMessageInterpolator);
  }

  @Bean
  @ConditionalOnMissingBean
  public ConstraintViolationExceptionHandler restConstraintViolationExceptionHandler(MessageSource messageSource,
      SpelMessageInterpolator spelMessageInterpolator) {
    return new ConstraintViolationExceptionHandler(messageSource, spelMessageInterpolator);
  }

  @Bean
  @ConditionalOnMissingBean
  public MethodArgumentNotValidExceptionHandler restMethodArgNotValidException(MessageSource messageSource,
      SpelMessageInterpolator spelMessageInterpolator) {
    return new MethodArgumentNotValidExceptionHandler(messageSource, spelMessageInterpolator);
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasenames("classpath:default-exception-messages", "classpath:spring-exception-messages",
        "classpath:messages", "classpath:app-messages");
    messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
    return messageSource;
  }

  @Bean
  @ConditionalOnMissingBean
  public SpelMessageInterpolator spelMessageInterpolator() {
    return new SpelMessageInterpolator();
  }

  @Bean
  @ConditionalOnMissingBean
  public RestHandlerExceptionResolver<Exception> restExceptionResolver(
      RestExceptionHandler<Exception> restExceptionHandler,
      ConstraintViolationExceptionHandler violationExceptionHandler,
      MethodArgumentNotValidExceptionHandler argumentNotValidExceptionHandler) {

    RestHandlerExceptionResolver<Exception> restHandlerExceptionResolver = new RestHandlerExceptionResolver<>();
    restHandlerExceptionResolver.addHandler(MethodArgumentNotValidException.class, argumentNotValidExceptionHandler);
    restHandlerExceptionResolver.addHandler(ConstraintViolationException.class, violationExceptionHandler);
    restHandlerExceptionResolver.addHandler(Exception.class, restExceptionHandler);
    return restHandlerExceptionResolver;
  }

  @Bean
  @ConditionalOnMissingBean(annotation = ControllerAdvice.class)
  public RestExceptionHandlerController restExceptionHandlerController(
      RestHandlerExceptionResolver<Exception> restHandlerExceptionResolver) {
    return new RestExceptionHandlerController(restHandlerExceptionResolver);
  }
}