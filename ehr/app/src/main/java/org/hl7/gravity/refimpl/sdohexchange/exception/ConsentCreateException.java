package org.hl7.gravity.refimpl.sdohexchange.exception;

/**
 * Thrown when something happened during Consent resource creation.
 *
 * @author Mykhailo Stefantsiv
 */
public class ConsentCreateException extends RuntimeException {

  public ConsentCreateException(String message) {
    super(message);
  }
}
