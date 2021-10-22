package org.hl7.gravity.refimpl.sdohexchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TaskUpdateException extends RuntimeException {

  public TaskUpdateException(String message) {
    super(message);
  }
}
