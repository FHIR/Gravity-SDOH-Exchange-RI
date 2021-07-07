package org.hl7.gravity.refimpl.sdohexchange.config.exceptions;

import org.hl7.gravity.refimpl.sdohexchange.exception.MessageSourcePropertyNotFoundException;
import org.hl7.gravity.refimpl.sdohexchange.handlers.MessageSourceExceptionHandler;
import org.hl7.gravity.refimpl.sdohexchange.handlers.RestExceptionHandler;
import org.hl7.gravity.refimpl.sdohexchange.interpolators.SpelMessageInterpolator;
import org.hl7.gravity.refimpl.sdohexchange.messages.ErrorMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Configuration
public class RestExceptionHandlerConfiguration extends ResponseEntityExceptionHandler {

  @Bean
  public RestExceptionHandler<Exception> restExceptionHandler(MessageSource messageSource,
      SpelMessageInterpolator spelMessageInterpolator) {
    return new MessageSourceExceptionHandler<Exception>(messageSource, spelMessageInterpolator) {
      @Override
      public ResponseEntity<ErrorMessage> handleException(Exception ex) throws MessageSourcePropertyNotFoundException {
        ResponseEntity<ErrorMessage> errorMessageResponseEntity = super.handleException(ex);
        if (errorMessageResponseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
          if (RestExceptionHandlerConfiguration.this.logger.isWarnEnabled()) {
            RestExceptionHandlerConfiguration.this.logger.warn("Unexpected Server Error", ex);
          }
        }
        return errorMessageResponseEntity;
      }
    };
  }
}