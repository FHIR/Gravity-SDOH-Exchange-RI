package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.springframework.core.convert.converter.Converter;

public class UserInfoToDtoConverter implements Converter<Map<String, Object>, UserDto> {

  @Override
  public UserDto convert(Map<String, Object> claims) {
    UserDto userDto = new UserDto();
    String fhirUser = getClaimValue(claims, "fhirUser");
    if (fhirUser != null) {
      userDto.setId(StringUtils.substringAfter(fhirUser, "/"));
      userDto.setUserType(StringUtils.substringBefore(fhirUser, "/"));
    }
    userDto.setName(getClaimValue(claims, "displayName"));
    userDto.setEmail(getClaimValue(claims, "email"));
    return userDto;
  }

  private String getClaimValue(Map<String, Object> claims, String claim) {
    Object claimValue = claims.get(claim);
    return claimValue == null ? null : claimValue.toString();
  }
}