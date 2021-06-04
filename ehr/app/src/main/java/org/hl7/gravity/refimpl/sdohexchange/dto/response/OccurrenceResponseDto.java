package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OccurrenceResponseDto {

  private final LocalDateTime start;
  private final LocalDateTime end;
}