package org.hl7.gravity.refimpl.sdohexchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthClientException extends Exception {

  public AuthClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
