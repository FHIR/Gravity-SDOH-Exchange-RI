package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.DateType;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class NewGoalDto {

  @NotEmpty(message = "Goal name can't be empty.")
  private String name;
  @NotEmpty(message = "SDOH category can't be empty.")
  private String category;
  @NotEmpty(message = "SNOMED code can't be empty.")
  private String snomedCode;

  private List<String> problemIds = new ArrayList<>();

  private LocalDate startDate;
  private String comment;

  public DateType getStart() {
    return startDate != null ? new DateType(Date.from(startDate.atStartOfDay(ZoneId.systemDefault())
        .toInstant())) : null;
  }
}
