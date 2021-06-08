package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OccurrenceResponseDto {

  private LocalDateTime start;
  private LocalDateTime end;
}