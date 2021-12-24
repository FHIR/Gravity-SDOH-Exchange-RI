package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ActiveResourcesDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConditionDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalInfoDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.HealthcareServiceDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OrganizationDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;
import org.hl7.gravity.refimpl.sdohexchange.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("support")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.SUPPORT_API_TAG})
public class SupportController {

  private final SupportService supportService;

  @GetMapping("conditions")
  @ApiOperation(value = "List active Problems (Condition FHIR resources of problem-list-item category).",
      notes = "Returned instances belong to a selected Patient and their category "
          + "field references a code from a code system 'http://hl7"
          + ".org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes'. Other Conditions are ignored! "
          + "Consider looking at 'errors' response field which holds additional conformance and validation checks.")
  public List<ConditionDto> listConditions() {
    return supportService.listProblems();
  }

  @GetMapping("goals")
  @ApiOperation(value = "List active Goal FHIR resources.",
      notes = "Returned instances belong to a selected Patient and their category "
          + "field references a code from a code system 'http://hl7"
          + ".org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes'. Other Goals are ignored! Consider "
          + "looking at 'errors' response field which holds additional conformance and validation checks.")
  public List<GoalInfoDto> listGoals() {
    return supportService.listGoals();
  }

  @GetMapping("organizations")
  @ApiOperation(value = "List all CBO/CBRO Organizations.",
      notes = "List all Organizations of type with system 'http://hl7"
          + ".org/gravity/CodeSystem/sdohcc-temporary-organization-type-codes' and code one of 'cbo' or 'cbro'. "
          + "Consider looking at 'errors' response field which holds additional conformance and validation checks.")
  public List<OrganizationDto> listOrganizations() {
    return supportService.listOrganizations();
  }

  @GetMapping("healthcare-services")
  @ApiOperation(value = "List all HealthcareService resources for specific Organization.",
      notes = "Consider looking at 'errors' response field which holds additional conformance and validation checks.")
  public List<HealthcareServiceDto> listHealthcareServices(String organizationId) {
    return supportService.listHealthcareServices(organizationId);
  }

  @GetMapping("assessments")
  @ApiOperation(value = "List all Questionnaire resources.")
  public List<ReferenceDto> listAssessments() {
    return supportService.listAssessments();
  }

  @GetMapping("activeResources")
  @ApiOperation(value = "List the counts of all active resources.",
      notes = "Returns the count of active Health Concerns, Problems, Goals and Action Steps.")
  public ActiveResourcesDto activeResources() {
    return supportService.getActiveResources();
  }

}