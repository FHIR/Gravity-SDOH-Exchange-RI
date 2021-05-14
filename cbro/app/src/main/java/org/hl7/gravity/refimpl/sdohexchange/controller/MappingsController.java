package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.System;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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

  @GetMapping("/categories/{code}/procedure/codings")
  @ApiOperation(value = "Get Procedure codings based on SDOH category.")
  public List<CodingDto> procedureCodings(@PathVariable String code) {
    return sdohMappings.getSystems(code, Procedure.class)
        .stream()
        .map(System::getCodings)
        .flatMap(List::stream)
        .map(coding -> new CodingDto(coding.getCode(), coding.getDisplay()))
        .collect(Collectors.toList());
  }
}