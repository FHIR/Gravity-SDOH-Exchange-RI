package org.hl7.gravity.refimpl.sdohexchange.exception;

/**
 * Exception represent an error occurred if Problem creation is failed.
 */
public class ProblemCreateException extends RuntimeException {

  public ProblemCreateException(String message) {
    super(message);
  }
}
