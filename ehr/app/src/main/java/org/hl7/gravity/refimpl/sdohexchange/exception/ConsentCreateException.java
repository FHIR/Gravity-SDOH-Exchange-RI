package org.hl7.gravity.refimpl.sdohexchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when something happened during Consent resource creation.
 *
 * @author Mykhailo Stefantsiv
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ConsentCreateException extends RuntimeException {

  public ConsentCreateException(String message) {
    super(message);
  }
}
