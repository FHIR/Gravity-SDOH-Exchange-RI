package org.hl7.gravity.refimpl.sdohexchange.exceptions;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Mykhailo Stefantsiv
 */
@RestControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    Error error = Error.builder()
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(String.format("Validation failed for object='%s'. Error count: %d", ex.getBindingResult()
            .getObjectName(), ex.getBindingResult()
            .getErrorCount()))
        .errors(ex.getBindingResult()
            .getAllErrors()
            .stream()
            .map(ObjectError::getDefaultMessage)
            .toArray(String[]::new))
        .path(((ServletWebRequest) request).getRequest()
            .getRequestURI())
        .build();
    //TODO We might want to consider server.error.include-stacktrace setting one day.
    return handleExceptionInternal(ex, error, headers, status, request);
  }

  @ExceptionHandler(value = {ResourceNotFoundException.class})
  public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
    return handleExceptionInternal(ex, HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(value = {BaseServerResponseException.class})
  public ResponseEntity<Object> handleBaseServerResponseException(BaseServerResponseException ex, WebRequest request) {
    return handleExceptionInternal(ex, HttpStatus.resolve(ex.getStatusCode()), request);
  }

  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, HttpStatus status, WebRequest request) {
    Error error = Error.builder()
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(ex.getMessage())
        .path(((ServletWebRequest) request).getRequest()
            .getRequestURI())
        .build();
    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
  }
}