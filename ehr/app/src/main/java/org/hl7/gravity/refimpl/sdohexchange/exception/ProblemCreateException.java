package org.hl7.gravity.refimpl.sdohexchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception represent an error occurred if Problem creation is failed.
 */
@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class ProblemCreateException extends RuntimeException {

  public ProblemCreateException(String message) {
    super(message);
  }
}
