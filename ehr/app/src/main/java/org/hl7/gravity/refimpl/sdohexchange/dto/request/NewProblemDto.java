package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class NewProblemDto extends NewHealthConcernDto {
  private LocalDate startDate;
}
