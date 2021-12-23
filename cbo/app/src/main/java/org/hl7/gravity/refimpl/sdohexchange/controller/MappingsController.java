package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.gravity.refimpl.sdohexchange.sdohmappings.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

// Copied from cp/app/src/main/java/org/hl7/gravity/refimpl/sdohexchange/controller/
@Validated
@RestController
@RequestMapping("mappings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = "Available resources API")
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

  @GetMapping("/categories/{category}/procedure/codings")
  @ApiOperation(value = "Get Procedure codings based on SDOH category.")
  public List<CodingDto> procedureCodings(
      @PathVariable @NotBlank(message = "Category can't be empty.") String category) {
    return sdohMappings.findAllResourceCodings(category, Procedure.class)
        .stream()
        .map(coding -> new CodingDto(coding.getCode(), coding.getDisplay()))
        .collect(Collectors.toList());
  }
}
