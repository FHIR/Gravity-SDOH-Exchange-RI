package org.hl7.gravity.refimpl.sdohexchange.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class LaunchController {

  @GetMapping("/")
  public String index() {
    return "index.html";
  }
}