package org.hl7.gravity.refimpl.sdohexchange.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@JsonInclude(Include.NON_NULL)
public class Error {

  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();
  private int status;
  private String error;
  private String[] errors;
  private String message;
  private String path;
}