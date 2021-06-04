package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
abstract class PersonDto {

  private String id;
  private String name;
}