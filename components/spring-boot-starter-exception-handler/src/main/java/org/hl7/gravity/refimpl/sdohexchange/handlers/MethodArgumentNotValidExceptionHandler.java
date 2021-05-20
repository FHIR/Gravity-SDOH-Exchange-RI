package org.hl7.gravity.refimpl.sdohexchange.handlers;

import org.hl7.gravity.refimpl.sdohexchange.exception.MessageSourcePropertyNotFoundException;
import org.hl7.gravity.refimpl.sdohexchange.interpolators.SpelMessageInterpolator;
import org.hl7.gravity.refimpl.sdohexchange.messages.ErrorMessage;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Specific {@link MessageSourceExceptionHandler} implementation for {@link MethodArgumentNotValidException}.
 */
public class MethodArgumentNotValidExceptionHandler
    extends MessageSourceExceptionHandler<MethodArgumentNotValidException> {

  /**
   * Constructor with parameters.
   *
   * @param messageSource {@link MessageSource} for exceptions error messages
   * @param interpolator  {@link SpelMessageInterpolator} for parsing exceptions error messages
   */
  public MethodArgumentNotValidExceptionHandler(MessageSource messageSource, SpelMessageInterpolator interpolator) {
    super(messageSource, interpolator);
  }

  @Override
  protected ErrorMessage createBody(MethodArgumentNotValidException ex) throws MessageSourcePropertyNotFoundException {
    ErrorMessage errorMessage = super.createBody(ex);
    BindingResult result = ex.getBindingResult();

    int excCount = 0;
    String detail = "";
    for (FieldError err : result.getFieldErrors()) {
      detail = String.format("%s %d.Field '%s' %s;", detail, ++excCount, err.getField(), err.getDefaultMessage());
    }
    for (ObjectError err : result.getGlobalErrors()) {
      detail = String.format("%s %d.%s;", detail, ++excCount, err.getDefaultMessage());
    }
    return errorMessage.setDetail(errorMessage.getDetail() + detail);
  }
}