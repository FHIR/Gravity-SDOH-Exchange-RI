package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CurrentContextDto;
import org.hl7.gravity.refimpl.sdohexchange.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.CONTEXT_API_TAG})
public class ContextController {

  private final ContextService contextService;

  @GetMapping("/current-context")
  @ApiOperation(value = "Details about a logged in user and selected Patient instance.", notes =
      "Get currently logged in user (usually a Practitioner) and a Patient selected during a launch. All operations "
          + "will be performed on this Patient instance (for example creation of a Task).")
  public CurrentContextDto getCurrentContext(@ApiIgnore @AuthenticationPrincipal OidcUser user) {
    return contextService.getCurrentContext();
  }
}