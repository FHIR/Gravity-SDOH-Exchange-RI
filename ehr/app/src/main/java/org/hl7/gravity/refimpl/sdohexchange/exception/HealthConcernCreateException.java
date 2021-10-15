package org.hl7.gravity.refimpl.sdohexchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception represent an error occurred if Health Concern creation is failed.
 */
@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class HealthConcernCreateException extends RuntimeException {

  public HealthConcernCreateException(String message) {
    super(message);
  }
}
