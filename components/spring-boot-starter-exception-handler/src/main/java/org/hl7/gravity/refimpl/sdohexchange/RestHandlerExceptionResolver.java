package org.hl7.gravity.refimpl.sdohexchange;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.hl7.gravity.refimpl.sdohexchange.exception.ErrorHandlerException;
import org.hl7.gravity.refimpl.sdohexchange.handlers.RestExceptionHandler;
import org.hl7.gravity.refimpl.sdohexchange.messages.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * A rest exception handler resolver for RESTful APIs that resolves exceptions through the provided {@link
 * RestExceptionHandler RestExceptionHandlers}.
 *
 * @param <E> Type of the handled exception.
 */
@Slf4j
public class RestHandlerExceptionResolver<E extends Exception> {

  public static final String DEFAULT_TYPE = "http://httpstatus.es/500";
  public static final String DEFAULT_TITLE = "Internal Server Error";
  public static final String DEFAULT_DETAIL =
      "The server encountered an internal error or misconfiguration and was unable to complete your request.";
  public static final Integer DEFAULT_HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR.value();

  private final Map<Class, RestExceptionHandler> exceptionHandlers;

  public RestHandlerExceptionResolver() {
    this.exceptionHandlers = new HashMap<>();
  }

  /**
   * Method finds first applicable exception handler and handles exception for a request.
   *
   * @param exception exception to generate a response for
   * @return {@link ResponseEntity} value
   */
  public ResponseEntity<ErrorMessage> handleException(E exception) {
    log.debug("An attempt to handle the exception that was thrown.", exception);
    RestExceptionHandler<E> handler = resolveExceptionHandler(exception.getClass());
    ResponseEntity<ErrorMessage> response;
    try {
      response = handler.handleException(exception);
    } catch (ErrorHandlerException e) {
      log.warn(String.format("Handler for '%s' cannot process exception. Default error response will be returned.",
          exception.getClass()
              .getName()), e);
      response = defaultResponse();
    }
    return response;
  }

  /**
   * Registers an exception handler for the specified exception type. This handler will be also used for all the
   * exception supertypes, when no more specific mapping is found.
   *
   * @param exceptionClass   The exception type handled by the given handler.
   * @param exceptionHandler An instance of the exception handler for the specified exception type or its supertypes.
   */
  public void addHandler(Class<? extends Exception> exceptionClass,
      RestExceptionHandler<? extends Exception> exceptionHandler) {
    exceptionHandlers.put(exceptionClass, exceptionHandler);
  }

  @SuppressWarnings("unchecked")
  protected RestExceptionHandler<E> resolveExceptionHandler(Class<? extends Exception> exceptionClass) {
    for (Class clazz = exceptionClass; clazz != Throwable.class; clazz = clazz.getSuperclass()) {
      if (exceptionHandlers.containsKey(clazz)) {
        return exceptionHandlers.get(clazz);
      }
    }
    log.warn("Handler for '{}' cannot be found. Default error response will be returned.", exceptionClass.getName());
    return exception -> defaultResponse();
  }

  /**
   * Generates default {@link ResponseEntity} item if handler cannot be found for exception, or error occurred during
   * handling mechanism.
   *
   * @return default response entity item
   */
  protected ResponseEntity<ErrorMessage> defaultResponse() {
    ErrorMessage errorMessage = ErrorMessage.build(URI.create(DEFAULT_TYPE), DEFAULT_HTTP_STATUS)
        .setTitle(DEFAULT_TITLE)
        .setDetail(DEFAULT_DETAIL);
    return ResponseEntity.status(DEFAULT_HTTP_STATUS)
        .body(errorMessage);
  }
}