package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.DateTimeType;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

//TODO copy from EHR app. Refactor.
@Getter
@Setter
public class OccurrenceRequestDto {

  private LocalDateTime start;
  @NotNull
  private LocalDateTime end;

  public boolean isPeriod() {
    return start != null && end != null;
  }

  public DateTimeType getStart() {
    return start != null ? new DateTimeType(Date.from(start.atZone(ZoneOffset.systemDefault())
        .toInstant())) : null;
  }

  public DateTimeType getEnd() {
    return new DateTimeType(Date.from(end.atZone(ZoneOffset.systemDefault())
        .toInstant()));
  }
}
