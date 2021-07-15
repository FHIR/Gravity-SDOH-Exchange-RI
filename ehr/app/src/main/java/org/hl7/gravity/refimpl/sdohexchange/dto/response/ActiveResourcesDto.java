package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActiveResourcesDto {

  private Integer activeConcernsCount;
  private Integer activeProblemsCount;
  private Integer activeGoalsCount;
  private Integer activeInterventionsCount;
}