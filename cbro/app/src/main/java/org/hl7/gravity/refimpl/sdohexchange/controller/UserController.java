package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.UserInfoToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@Api(tags = {SpringFoxConfig.USER_API_TAG})
public class UserController {

  @GetMapping("/user-info")
  @ApiOperation(value = "Info of a currently logged in user.")
  public UserDto userInfo(@ApiIgnore @AuthenticationPrincipal OidcUser user) {
    return new UserInfoToDtoConverter().convert(user.getClaims());
  }
}