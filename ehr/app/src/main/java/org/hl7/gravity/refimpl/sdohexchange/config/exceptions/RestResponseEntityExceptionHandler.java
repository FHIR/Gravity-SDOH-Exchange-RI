package org.hl7.gravity.refimpl.sdohexchange.config.exceptions;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Mykhailo Stefantsiv
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  //TODO: implement advanced exception handling mechanism
  @ExceptionHandler(value = {ResourceNotFoundException.class})
  protected ResponseEntity<Object> handleResourceNotFound(RuntimeException ex, WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  private class ErrorMessage {

    private Integer status;
    private String detail;
  }
}