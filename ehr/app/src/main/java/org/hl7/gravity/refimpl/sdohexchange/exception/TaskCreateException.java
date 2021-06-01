package org.hl7.gravity.refimpl.sdohexchange.exception;

/**
 * Exception represent an error occurred if Task creation is failed.
 */
public class TaskCreateException extends RuntimeException {

  public TaskCreateException(String message) {
    super(message);
  }
}
