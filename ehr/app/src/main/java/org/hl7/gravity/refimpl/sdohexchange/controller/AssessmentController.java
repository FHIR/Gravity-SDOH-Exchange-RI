package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.AssessmentDto;
import org.hl7.gravity.refimpl.sdohexchange.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assessment")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.ASSESSMENT_API_TAG})
public class AssessmentController {

  private final AssessmentService assessmentService;

  @ApiOperation("Retrieve past social risk assessments.")
  @GetMapping("/past")
  public List<AssessmentDto> pastAssessments() {
    return assessmentService.listCompleted();
  }

  @ApiOperation("Read social risk assessment by questionnaire canonical url.")
  @GetMapping
  public AssessmentDto readQuestionnaire(@RequestParam @NotBlank String questionnaireUrl) {
    return assessmentService.search(questionnaireUrl);
  }
}