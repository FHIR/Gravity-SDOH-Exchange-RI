package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OrganizationDto;
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

  @GetMapping("organizations")
  @ApiOperation(value = "List all CBO Organizations.", notes = "List all Organizations of type with system 'http://hl7"
      + ".org/gravity/CodeSystem/sdohcc-temporary-organization-type-codes' and code 'cbo'. "
      + "Consider looking at 'errors' response field which holds additional conformance and validation checks.")
  public List<OrganizationDto> listOrganizations() {
    return supportService.listOrganizations();
  }

}