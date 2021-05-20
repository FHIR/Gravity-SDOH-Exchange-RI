package org.hl7.gravity.refimpl.sdohexchange.handlers;

import org.hl7.gravity.refimpl.sdohexchange.exception.MessageSourcePropertyNotFoundException;
import org.hl7.gravity.refimpl.sdohexchange.interpolators.SpelMessageInterpolator;
import org.hl7.gravity.refimpl.sdohexchange.messages.ErrorMessage;
import org.hl7.gravity.refimpl.sdohexchange.messages.ErrorMessageBuilder;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * {@link RestExceptionHandler} that works with {@link MessageSource} and produces {@link ErrorMessage}.
 *
 * @param <E> Type of the handled exception.
 */
public class MessageSourceExceptionHandler<E extends Exception> implements RestExceptionHandler<E> {

  private final MessageSource messageSource;
  private final SpelMessageInterpolator interpolator;

  /**
   * Constructor with parameters.
   *
   * @param messageSource {@link MessageSource} for exceptions error messages
   * @param interpolator  {@link SpelMessageInterpolator} for parsing exceptions error messages
   */
  public MessageSourceExceptionHandler(MessageSource messageSource, SpelMessageInterpolator interpolator) {
    this.messageSource = messageSource;
    this.interpolator = interpolator;
  }

  @Override
  public ResponseEntity<ErrorMessage> handleException(E ex) throws MessageSourcePropertyNotFoundException {
    ErrorMessage body = createBody(ex);
    return new ResponseEntity<>(body, HttpStatus.valueOf(body.getStatus()));
  }

  protected ErrorMessage createBody(E ex) throws MessageSourcePropertyNotFoundException {
    ErrorMessageBuilder<E> messageBuilder = new ErrorMessageBuilder<>();
    return messageBuilder.messageSource(messageSource)
        .interpolator(interpolator)
        .build(ex);
  }
}