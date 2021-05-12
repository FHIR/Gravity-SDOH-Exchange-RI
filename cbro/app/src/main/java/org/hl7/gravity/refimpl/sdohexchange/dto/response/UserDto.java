package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Data;

@Data
public class UserDto {
  private String id;
  private String userType;
  private String name;
  private String email;
}
