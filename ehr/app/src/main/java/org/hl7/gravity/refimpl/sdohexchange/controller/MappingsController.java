package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.gravity.refimpl.sdohexchange.sdohmappings.Coding;
import org.hl7.gravity.refimpl.sdohexchange.sdohmappings.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.sdohmappings.System;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.SystemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("mappings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.MAPPINGS_API_TAG})
public class MappingsController {

  private final SDOHMappings sdohMappings;

  @GetMapping("/categories")
  @ApiOperation(
      value = "This value set is defined extensionally to include concepts that represent Social Determinant of "
          + "Health domains.")
  public List<CodingDto> categories() {
    return sdohMappings.getCategories()
        .stream()
        .map(category -> new CodingDto(category.getCode(), category.getDisplay()))
        .collect(Collectors.toList());
  }

  @GetMapping("/categories/{category}/servicerequest/codings")
  @ApiOperation(value = "Get ServiceRequest codings based on SDOH category.")
  public List<CodingDto> procedureCodings(
      @PathVariable @NotBlank(message = "Category can't be empty.") String category) {
    return toCodingDto(sdohMappings.findAllResourceCodings(category, ServiceRequest.class));
  }

  @GetMapping("/categories/{category}/condition/codings")
  @ApiOperation(value = "Get Condition codings based on SDOH category for SNOMED and ICD-10 systems.")
  public List<SystemDto> conditionCodings(
      @PathVariable @NotBlank(message = "Category can't be empty.") String category) {
    return sdohMappings.findResourceSystems(category, Condition.class, Arrays.asList(System.SNOMED, System.ICD_10))
        .stream()
        .map(system -> new SystemDto(system.getSystem(), system.getDisplay(), toCodingDto(system.getCodings())))
        .collect(Collectors.toList());
  }

  @GetMapping("/categories/{category}/goal/codings")
  @ApiOperation(value = "Get Goal codings based on SDOH category for SNOMED and ICD-10 systems.")
  public List<SystemDto> goalCodings(@PathVariable @NotBlank(message = "Category can't be empty.") String category) {
    return sdohMappings.findResourceSystems(category, Goal.class, Arrays.asList(System.SNOMED, System.ICD_10))
        .stream()
        .map(system -> new SystemDto(system.getSystem(), system.getDisplay(), toCodingDto(system.getCodings())))
        .collect(Collectors.toList());
  }

  private static List<CodingDto> toCodingDto(List<Coding> codings) {
    return codings.stream()
        .map(coding -> new CodingDto(coding.getCode(), coding.getDisplay()))
        .collect(Collectors.toList());
  }
}