package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemDto {

  private String system;
  private String display;
  private List<CodingDto> codings;
}