package org.hl7.gravity.refimpl.sdohexchange.messages;

import java.net.URI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Error response item.
 */
@Setter
@Getter
@Accessors(chain = true)
@RequiredArgsConstructor
public class ErrorMessage {

  private final URI type;
  private final Integer status;
  private String title;
  private String detail;

  public static ErrorMessage build(URI type, Integer status) {
    return new ErrorMessage(type, status);
  }
}