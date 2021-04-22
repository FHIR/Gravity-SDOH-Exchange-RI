package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("task")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
//TODO: To be implemented
public class TaskController {

  private final TaskService taskService;

  @GetMapping
  @ApiOperation(value = "List all Task resources.")
  public List<TaskDto> list() {
    return taskService.listTasks();
  }

  @GetMapping("/{taskId}")
  @ApiOperation(value = "Read Task resource by id.")
  public ResponseEntity<TaskDto> task(@PathVariable String taskId) {
    TaskDto taskDto = taskService.readTask(taskId);
    return taskDto == null ? ResponseEntity.notFound()
        .build() : ResponseEntity.ok(taskDto);
  }
}