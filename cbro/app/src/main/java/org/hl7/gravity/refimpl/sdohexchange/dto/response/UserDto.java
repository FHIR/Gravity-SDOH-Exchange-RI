package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Data;

@Data
public class UserDto {
  private String givenName;
  private String familyName;
  private String name;
  private String preferredUsername;
}
