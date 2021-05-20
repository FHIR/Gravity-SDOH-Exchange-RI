package org.hl7.gravity.refimpl.sdohexchange.messages;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.hl7.gravity.refimpl.sdohexchange.exception.MessageSourcePropertyNotFoundException;
import org.hl7.gravity.refimpl.sdohexchange.interpolators.SpelMessageInterpolator;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

/**
 * Builder for {@link org.hl7.gravity.refimpl.sdohexchange.handlers.MessageSourceExceptionHandler} error message. This
 * class is super private and is not valid for extensions. Refactor it when needed.
 *
 * @param <E> Type of the handled exception.
 */
@Setter
@Accessors(fluent = true)
@Slf4j
@SuppressWarnings("unchecked")
public class ErrorMessageBuilder<E extends Exception> {

  private static final Integer DEFAULT_HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR.value();
  private static final String TYPE_KEY = "type";
  private static final String TITLE_KEY = "title";
  private static final String DETAIL_KEY = "detail";
  private static final String STATUS = "status";

  private MessageSource messageSource;
  private SpelMessageInterpolator interpolator;

  /**
   * Builds error message for {@link org.springframework.http.ResponseEntity} response body.
   *
   * @param exception handled exception
   * @return error message
   */
  public ErrorMessage build(E exception) throws MessageSourcePropertyNotFoundException {
    String prefix = resolveMessagePrefix(exception.getClass());
    return ErrorMessage.build(URI.create(resolveMessage(prefix, TYPE_KEY, exception)), resolveStatus(prefix))
        .setTitle(resolveMessage(prefix, TITLE_KEY, exception))
        .setDetail(resolveMessage(prefix, DETAIL_KEY, exception));
  }

  private String resolveMessagePrefix(Class<? extends Exception> exceptionClass)
      throws MessageSourcePropertyNotFoundException {
    for (Class clazz = exceptionClass; clazz != Throwable.class; clazz = clazz.getSuperclass()) {
      String prefix = getMessage(clazz.getName(), TYPE_KEY);
      if (!StringUtils.isEmpty(prefix)) {
        return clazz.getName();
      }
    }
    throw new MessageSourcePropertyNotFoundException(exceptionClass.getName() + "." + TYPE_KEY);
  }

  private Integer resolveStatus(String prefix) {
    String status = getMessage(prefix, STATUS);
    if (StringUtils.isEmpty(status)) {
      log.warn("Http status was set as default '{}' for exception '{}' class.", DEFAULT_HTTP_STATUS, prefix);
      return DEFAULT_HTTP_STATUS;
    }
    return HttpStatus.valueOf(Integer.parseInt(status))
        .value();
  }

  private String resolveMessage(String prefix, String key, E exception) {
    String messageTemplate = getMessage(prefix, key);
    Map<String, Object> variables = new HashMap<>(1);
    variables.put("ex", exception);
    return interpolator.interpolate(messageTemplate, variables);
  }

  private String getMessage(String prefix, String key) {
    String message = messageSource.getMessage(prefix + "." + key, null, null, LocaleContextHolder.getLocale());
    if (message == null) {
      log.debug("No message found for '{}.{}'.", prefix, key);
    }
    return message;
  }
}