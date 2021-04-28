package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.apache.commons.lang3.StringUtils;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

public class UserInfoToDtoConverter implements Converter<Map<String, Object>, UserDto> {

  @Override
  public UserDto convert(Map<String, Object> claims) {
    UserDto userDto = new UserDto();
    String profile = getClaimValue(claims, "profile");
    if (profile != null) {
      userDto.setId(StringUtils.substringAfter(profile, "/"));
      userDto.setUserType(StringUtils.substringBefore(profile, "/"));
    }
    userDto.setName(getClaimValue(claims, "name"));
    userDto.setPreferredUsername(getClaimValue(claims, "preferred_username"));
    return userDto;
  }

  private String getClaimValue(Map<String, Object> claims, String claim) {
    Object claimValue = claims.get(claim);
    return claimValue == null ? null : claimValue.toString();
  }
}
