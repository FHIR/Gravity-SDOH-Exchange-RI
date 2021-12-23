package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.UserInfoToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.patienttask.NewPatientTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.NewTaskResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.patienttask.PatientTaskItemDto;
import org.hl7.gravity.refimpl.sdohexchange.service.PatientTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("patient-task")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.PATIENT_TASK_API_TAG})
public class PatientTaskController {

  private final PatientTaskService patientTaskService;

  @GetMapping
  @ApiOperation(value = "List all Patient Task resources.",
      notes = "This will return all patient Task resources. Task instances can be created manually as well "
          + "(not through a 'create' endpoint) - in this case an additional 'errors' field will point out existing "
          + "issues and help to fix them for proper processing.")
  public List<PatientTaskItemDto> list() {
    return patientTaskService.listTasks();
  }

  @PostMapping
  @ApiOperation(value = "Create a new Patient task.",
      notes = "This endpoint will fail if request is invalid or preconditions are not met. All details will be "
          + "provided in error response description.")
  public NewTaskResponseDto create(@RequestBody @Valid NewPatientTaskRequestDto newTaskRequestDto,
      @ApiIgnore @AuthenticationPrincipal OidcUser user) {
    //TODO replace with validator
    if (!newTaskRequestDto.getType()
        .getCodes()
        .stream()
        .anyMatch(c -> c.getCode()
            .equals(newTaskRequestDto.getCode()))) {
      throw new IllegalArgumentException(
          String.format("Code %s cannot be used with type %s.", newTaskRequestDto.getCode(), newTaskRequestDto.getType()
              .toString()));
    }
    UserDto userDto = new UserInfoToDtoConverter().convert(user.getClaims());
    return new NewTaskResponseDto(patientTaskService.newTask(newTaskRequestDto, userDto));
  }

}