package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends PersonDto {

  private String userType;
  private String email;
}
