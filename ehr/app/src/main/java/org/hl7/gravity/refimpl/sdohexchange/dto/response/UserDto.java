package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Getter;

@Getter
public class UserDto extends PersonDto {

  private final String userType;

  public UserDto(String id, String userType) {
    super(id);
    this.userType = userType;
  }
}