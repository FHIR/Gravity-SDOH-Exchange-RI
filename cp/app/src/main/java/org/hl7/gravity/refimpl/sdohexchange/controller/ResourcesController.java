package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskJsonResourcesDto;
import org.hl7.gravity.refimpl.sdohexchange.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Api(tags = {SpringFoxConfig.RESOURCES_API_TAG})
@Validated
@RequestMapping("/resources")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
public class ResourcesController {

  private final ResourceService resourceService;

  @ApiOperation(value = "Get all Task resources as JSON.")
  @GetMapping("/task/{id}")
  public TaskJsonResourcesDto getTaskResources(@PathVariable @NotBlank(message = "Task id can't be empty.") String id) {
    return resourceService.getTaskResources(id);
  }
}