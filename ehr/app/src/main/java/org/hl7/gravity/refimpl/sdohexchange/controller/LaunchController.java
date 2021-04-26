package org.hl7.gravity.refimpl.sdohexchange.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@Controller
@ApiIgnore
public class LaunchController {

  @GetMapping("/")
  public String index() {
    return "index.html";
  }
}
