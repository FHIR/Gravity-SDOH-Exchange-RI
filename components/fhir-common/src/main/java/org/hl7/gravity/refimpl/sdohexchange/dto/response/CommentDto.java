package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

  private TypeDto author;
  private LocalDateTime time;
  private String text;
}
