package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.UserInfoToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.UpdateTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
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
  @ApiOperation(value = "Retrieve all Task resources.")
  public List<TaskDto> readAll() {
    return taskService.readAll();
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "Retrieve Task resource by id.")
  public ResponseEntity<TaskDto> read(@PathVariable @NotBlank String id) {
    TaskDto taskDto = taskService.read(id);
    return taskDto == null ? ResponseEntity.notFound()
        .build() : ResponseEntity.ok(taskDto);
  }

  @PutMapping("/{id}")
  @ApiOperation(value = "Update Task resource by id.")
  public ResponseEntity<TaskDto> update(@PathVariable @NotBlank String id, @RequestBody @Valid UpdateTaskRequestDto updateTaskRequestDto,
      @ApiIgnore @AuthenticationPrincipal OidcUser user) {
    UserDto userDto = new UserInfoToDtoConverter().convert(user.getClaims());
    TaskDto taskDto = taskService.update(id, updateTaskRequestDto, userDto);
    return taskDto == null ? ResponseEntity.notFound()
        .build() : ResponseEntity.ok(taskDto);
  }
}