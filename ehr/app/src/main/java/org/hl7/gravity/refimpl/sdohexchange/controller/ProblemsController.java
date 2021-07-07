package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ProblemDto;
import org.hl7.gravity.refimpl.sdohexchange.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/problem")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.PROBLEM_API_TAG})
public class ProblemsController {

  private final ProblemService problemService;

  @GetMapping("/active")
  @ApiOperation(value = "List of all active Problems.")
  public List<ProblemDto> listActive() {
    return problemService.listActive();
  }
}