package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

// Copied from cp/app/src/main/java/org/hl7/gravity/refimpl/sdohexchange/dto/response/
@Getter
@Setter
@RequiredArgsConstructor
public class ServiceRequestDto {

  private String id;
  private CodingDto category;
  private CodingDto code;
  private OccurrenceResponseDto occurrence;
}
