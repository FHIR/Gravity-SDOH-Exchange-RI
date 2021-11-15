package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.UpdateTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.AuthClientException;
import org.hl7.gravity.refimpl.sdohexchange.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
    return taskService.readAll();
  }

  @GetMapping("/{serverId}/{taskId}")
  @ApiOperation(value = "Retrieve Task resource by id from specific server.")
  public TaskDto read(@PathVariable Integer serverId, @PathVariable String taskId) {
    return taskService.read(serverId, taskId);
  }

  @PutMapping("/{id}")
  @ApiOperation(value = "Update Task resource.")
  public ResponseEntity<Void> update(@PathVariable("id") String id,
      @RequestBody @Valid UpdateTaskRequestDto updateTaskRequestDto) throws AuthClientException {
    taskService.update(id, updateTaskRequestDto);
    return ResponseEntity.noContent()
        .build();
  }
}
