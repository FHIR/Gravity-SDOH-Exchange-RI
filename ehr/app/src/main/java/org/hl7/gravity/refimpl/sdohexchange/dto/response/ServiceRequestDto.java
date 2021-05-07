package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.gravity.refimpl.sdohexchange.dto.Validated;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ServiceRequestDto implements Validated {

  private final String id;
  private String category;
  private String requestCode;
  private OccurrenceResponseDto occurrence;
  private List<ConditionResponseDto> conditions = new ArrayList<>();
  private List<GoalResponseDto> goals = new ArrayList<>();
  private ConsentResponseDto consent;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();
}
