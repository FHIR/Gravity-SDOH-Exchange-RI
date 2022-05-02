package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.characteristic.PersonalCharacteristicJsonResourcesDto;
import org.hl7.gravity.refimpl.sdohexchange.service.ResourceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("resources")
@Api(tags = "Resources API")
public class ResourceController {

  private final ResourceService resourceService;

  @ApiOperation(value = "Get all Personal Characteristic resources as JSON.")
  @GetMapping("/personal-characteristics/{characteristicId}")
  public PersonalCharacteristicJsonResourcesDto getPersonalCharacteristicResources(
      @PathVariable @NotBlank(message = "Personal Characteristic id can't be empty.") String characteristicId) {
    return resourceService.getCharacteristicResources(characteristicId);
  }
}
