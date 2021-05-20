package org.hl7.gravity.refimpl.sdohexchange.handlers;

import org.hl7.gravity.refimpl.sdohexchange.exception.ErrorHandlerException;
import org.hl7.gravity.refimpl.sdohexchange.messages.ErrorMessage;
import org.springframework.http.ResponseEntity;

/**
 * Contract for classes generating a {@link ResponseEntity} for an instance of the specified Exception type, used in
 * {@link org.hl7.gravity.refimpl.sdohexchange.RestHandlerExceptionResolver}.
 *
 * @param <E> Type of the handled exception.
 */
@FunctionalInterface
public interface RestExceptionHandler<E extends Exception> {

  /**
   * Handles exception and generates {@link ResponseEntity}.
   *
   * @param exception The exception to handle and get data from.
   * @return A response entity.
   *
   * @throws ErrorHandlerException if exception cannot be handled
   */
  ResponseEntity<ErrorMessage> handleException(E exception) throws ErrorHandlerException;
}