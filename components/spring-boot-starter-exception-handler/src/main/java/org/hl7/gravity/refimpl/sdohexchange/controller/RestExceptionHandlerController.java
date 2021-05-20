package org.hl7.gravity.refimpl.sdohexchange.controller;

import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.RestHandlerExceptionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler.
 */
@RestControllerAdvice
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RestExceptionHandlerController {

  private final RestHandlerExceptionResolver<Exception> exceptionResolver;

  /**
   * Method to handle all {@link Exception}.
   *
   * @param ex exception to handle
   * @return error response
   */
  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity handleException(Exception ex) {
    return exceptionResolver.handleException(ex);
  }
}