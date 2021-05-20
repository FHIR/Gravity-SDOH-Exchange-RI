package org.hl7.gravity.refimpl.sdohexchange.exception;

import lombok.Getter;

/**
 * Exception to be thrown if message source property value is not set.
 */
@Getter
public class MessageSourcePropertyNotFoundException extends ErrorHandlerException {

  private static final long serialVersionUID = 3082083887562740063L;

  private final String property;

  /**
   * Constructor of the MessageSourcePropertyNotFoundException.
   *
   * @param property not found property
   */
  public MessageSourcePropertyNotFoundException(String property) {
    super(String.format("Property '%s' cannot be found. Verify your message source.", property));
    this.property = property;
  }
}