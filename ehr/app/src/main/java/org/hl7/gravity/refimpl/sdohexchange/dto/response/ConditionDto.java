package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.codesystems.ConditionState;
import org.hl7.fhir.r4.model.codesystems.ConditionVerStatus;
import org.hl7.gravity.refimpl.sdohexchange.dto.Validated;
import org.hl7.gravity.refimpl.sdohexchange.fhir.codesystems.SDOHDomainCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ConditionDto implements Validated {

  private final String conditionId;
  private ConditionState clinicalStatus;
  private ConditionVerStatus verificationStatus;
  //TODO support category
  //private String category;
  private SDOHDomainCode domain;
  private LocalDateTime dateRecorded;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();
}
