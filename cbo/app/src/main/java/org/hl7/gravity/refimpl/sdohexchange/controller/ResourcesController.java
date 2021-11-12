package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskJsonResourcesDto;
import org.hl7.gravity.refimpl.sdohexchange.service.ResourceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("resources")
@Api(tags = "Resources API")
public class ResourcesController {

  private final ResourceService resourceService;

  @ApiOperation(value = "Get all Task resources as JSON.")
  @GetMapping("/{serverId}/task/{taskId}")
  public TaskJsonResourcesDto getTaskResources(@PathVariable Integer serverId, @PathVariable String taskId) {
    return resourceService.getTaskResources(serverId, taskId);
  }
}
