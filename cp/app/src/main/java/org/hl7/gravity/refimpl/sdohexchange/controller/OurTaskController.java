package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.service.OurTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
@RestController
@RequestMapping("our-task")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.OUR_TASK_API_TAG})
public class OurTaskController {

  private final OurTaskService taskService;

  @GetMapping
  @ApiOperation(value = "Retrieve all Our Tasks.")
  public List<TaskDto> readAll() {
    return taskService.readAll();
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "Retrieve Task resource by id.")
  public TaskDto read(@PathVariable @NotBlank(message = "Task id can't be empty.") String id) {
    return taskService.read(id);
  }
}