package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.UserInfoToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.UpdateOurTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.service.OurTaskService;
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
@RequestMapping("our-task")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.OUR_TASK_API_TAG})
public class OurTaskController {

  private final OurTaskService ourTaskService;

  @GetMapping
  @ApiOperation(value = "Retrieve all Our Tasks.")
  public List<TaskDto> readAll() {
    return ourTaskService.readAll();
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "Retrieve Our Task resource by id.")
  public TaskDto read(@PathVariable @NotBlank(message = "Task id can't be empty.") String id) {
    return ourTaskService.read(id);
  }

  @PutMapping("/{id}")
  @ApiOperation(value = "Update Our Task resource.")
  public ResponseEntity<Void> update(@PathVariable @NotBlank(message = "Task id can't be empty.") String id,
      @RequestBody @Valid UpdateOurTaskRequestDto updateOurTaskRequestDto,
      @ApiIgnore @AuthenticationPrincipal OidcUser user) {
    UserDto userDto = new UserInfoToDtoConverter().convert(user.getClaims());
    ourTaskService.update(id, updateOurTaskRequestDto, userDto);
    return ResponseEntity.noContent()
        .build();
  }
}