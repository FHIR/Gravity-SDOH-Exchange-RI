package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.UserInfoToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.NewTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.UpdateTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.NewTaskResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("task")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.TASK_API_TAG})
public class TaskController {

  private final TaskService taskService;

  @PostMapping
  @ApiOperation(value = "Create a task.",
      notes = "Use Support Controller endpoints to get proper references for Conditions, Goals, Organizations, "
          + "etc. This endpoint will fail if request is invalid or preconditions are not met. All details will be "
          + "provided in error response description. In some cases a Task creation might succeed but a Task of a "
          + "status will be set to FAILED. Refer to an 'outcome' field for more details.")
  public NewTaskResponseDto create(@RequestBody @Valid NewTaskRequestDto newTaskRequestDto,
      @ApiIgnore @AuthenticationPrincipal OidcUser user) {
    UserDto userDto = new UserInfoToDtoConverter().convert(user.getClaims());
    return new NewTaskResponseDto(taskService.newTask(newTaskRequestDto, userDto));
  }

  @GetMapping
  @ApiOperation(value = "List all Task resources.",
      notes = "This will include ALL available Task resources, even the ones created outside of a "
          + "SDOH use case. Task/ServiceRequest instances can be created manually as well (not through a 'create' "
          + "endpoint) - in this case an additional 'errors' field will point out existing issues and help to fix "
          + "them for proper processing.")
  public List<TaskDto> list() {
    return taskService.listTasks();
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "Read Task resource by id.")
  public TaskDto read(@PathVariable @NotBlank(message = "") String id) {
    return taskService.read(id);
  }

  @PutMapping("{id}")
  @ApiOperation(value = "Update Task resource.")
  public ResponseEntity<Void> update(@PathVariable @NotBlank(message = "Task id can't be empty.") String id,
      @Valid @RequestBody UpdateTaskRequestDto update, @ApiIgnore @AuthenticationPrincipal OidcUser user) {
    UserDto userDto = new UserInfoToDtoConverter().convert(user.getClaims());
    taskService.update(id, update, userDto);
    return ResponseEntity.noContent()
        .build();
  }
}