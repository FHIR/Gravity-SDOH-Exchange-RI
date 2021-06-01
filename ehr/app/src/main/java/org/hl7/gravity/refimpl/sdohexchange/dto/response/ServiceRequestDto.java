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
  private CodingDto category;
  private CodingDto code;
  private OccurrenceResponseDto occurrence;
  private List<TypeDto> conditions = new ArrayList<>();
  private List<TypeDto> goals = new ArrayList<>();
  private TypeDto consent;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();
}
