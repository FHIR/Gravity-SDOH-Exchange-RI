package org.hl7.gravity.refimpl.sdohexchange.handlers;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.hl7.gravity.refimpl.sdohexchange.exception.MessageSourcePropertyNotFoundException;
import org.hl7.gravity.refimpl.sdohexchange.interpolators.SpelMessageInterpolator;
import org.hl7.gravity.refimpl.sdohexchange.messages.ErrorMessage;
import org.springframework.context.MessageSource;

/**
 * Specific {@link MessageSourceExceptionHandler} implementation for {@link ConstraintViolationException}.
 */
public class ConstraintViolationExceptionHandler extends MessageSourceExceptionHandler<ConstraintViolationException> {

  /**
   * Constructor with parameters.
   *
   * @param messageSource {@link MessageSource} for exceptions error messages
   * @param interpolator  {@link SpelMessageInterpolator} for parsing exceptions error messages
   */
  public ConstraintViolationExceptionHandler(MessageSource messageSource, SpelMessageInterpolator interpolator) {
    super(messageSource, interpolator);
  }

  @Override
  protected ErrorMessage createBody(ConstraintViolationException ex) throws MessageSourcePropertyNotFoundException {
    ErrorMessage errorMessage = super.createBody(ex);

    String detail = "";
    int excCount = 0;
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      detail = String.format("%s %d. %s;", detail, ++excCount, violation.getMessage());
    }
    return errorMessage.setDetail(errorMessage.getDetail() + detail);
  }
}