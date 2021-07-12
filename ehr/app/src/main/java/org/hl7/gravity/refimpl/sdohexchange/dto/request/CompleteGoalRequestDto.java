package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.DateType;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
public class CompleteGoalRequestDto {

  @NotNull
  private LocalDate endDate;

  public DateType getEnd() {
    return endDate != null ? new DateType(Date.from(endDate.atStartOfDay(ZoneId.systemDefault())
        .toInstant())) : null;
  }
}
