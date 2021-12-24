package org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CommentDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class PatientTaskDto extends PatientTaskItemDto {

  private LocalDateTime createdAt;
  private List<CommentDto> comments = new ArrayList<>();

}
