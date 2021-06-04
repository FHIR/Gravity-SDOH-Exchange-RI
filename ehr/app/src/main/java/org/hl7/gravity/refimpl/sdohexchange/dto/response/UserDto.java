package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends PersonDto {

  private String userType;
  private String email;
}