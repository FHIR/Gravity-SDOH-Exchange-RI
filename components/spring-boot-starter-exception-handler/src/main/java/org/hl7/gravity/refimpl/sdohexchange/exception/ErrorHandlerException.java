package org.hl7.gravity.refimpl.sdohexchange.exception;

/**
 * Exception to be thrown if {@link org.hl7.gravity.refimpl.sdohexchange.handlers.RestExceptionHandler} cannot handle
 * current exception.
 */
public class ErrorHandlerException extends Exception {

  private static final long serialVersionUID = 5086334675962769992L;

  /**
   * Constructor of the ErrorHandlerException.
   *
   * @param message exception message title
   * @param cause   exception
   */
  public ErrorHandlerException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor of the ErrorHandlerException.
   *
   * @param message exception message
   */
  public ErrorHandlerException(String message) {
    super(message);
  }
}