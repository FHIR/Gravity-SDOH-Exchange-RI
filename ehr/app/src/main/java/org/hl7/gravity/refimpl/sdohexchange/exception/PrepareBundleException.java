package org.hl7.gravity.refimpl.sdohexchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception represent an error occurred during prepare for Health Concern creation.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PrepareBundleException extends RuntimeException {

  public PrepareBundleException(String message) {
    super(message);
  }

  public PrepareBundleException(String message, Throwable cause) {
    super(message, cause);
  }
}
