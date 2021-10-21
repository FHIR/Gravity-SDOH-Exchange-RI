package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.AuthClientException;
import org.hl7.gravity.refimpl.sdohexchange.service.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("tasks")
@Api(tags = "Task API")
public class TaskController {

  private final TaskService taskService;

  @GetMapping
  @ApiOperation(value = "List of all tasks from all servers")
  public List<TaskDto> list() throws AuthClientException {
    return taskService.getTasks();
  }
}
