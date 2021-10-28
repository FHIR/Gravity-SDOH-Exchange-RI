package org.hl7.gravity.refimpl.sdohexchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TaskIntentException extends RuntimeException {

  public TaskIntentException(String message) {
    super(message);
  }
}
