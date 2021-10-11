package org.hl7.gravity.refimpl.sdohexchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateServerNameNotAllowedException extends RuntimeException {

  public DuplicateServerNameNotAllowedException(String name) {
    super(String.format("Server with '%s' name already exists.", name));
  }
}
