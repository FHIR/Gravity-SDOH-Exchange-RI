package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
@RestController
@RequestMapping("task")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.TASK_API_TAG})
public class TaskController {

  private final TaskService taskService;

  @GetMapping
  @ApiOperation(value = "List all Task resources.")
  public List<TaskDto> list() {
    return taskService.listTasks();
  }

  @GetMapping("/{taskId}")
  @ApiOperation(value = "Read Task resource by id.")
  public ResponseEntity<TaskDto> task(@PathVariable @NotBlank String taskId) {
    TaskDto taskDto = taskService.readTask(taskId);
    return taskDto == null ? ResponseEntity.notFound()
        .build() : ResponseEntity.ok(taskDto);
  }
}