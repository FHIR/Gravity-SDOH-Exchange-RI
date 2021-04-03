package org.hl7.gravity.refimpl.sdohexchange.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@Controller
@ApiIgnore
public class LaunchController {

  @GetMapping("/")
  public String index() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    OAuth2User principal = (OAuth2User) authentication.getPrincipal();
    System.out.println(principal.getAttributes());
    return "index";
  }
}
