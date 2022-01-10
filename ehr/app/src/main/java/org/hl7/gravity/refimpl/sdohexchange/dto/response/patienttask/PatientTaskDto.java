package org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CommentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OccurrenceResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PatientTaskDto extends PatientTaskItemDto {

  private LocalDateTime createdAt;
  private OccurrenceResponseDto occurrence;
  private List<CommentDto> comments = new ArrayList<>();
  private Map<String, String> answers = new HashMap<>();
}
