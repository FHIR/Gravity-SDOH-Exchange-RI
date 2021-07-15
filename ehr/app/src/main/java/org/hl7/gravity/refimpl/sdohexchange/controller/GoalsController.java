package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.UserInfoToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.CompleteGoalRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.NewGoalDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.validation.annotation.Validated;
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

@Validated
@RestController
@RequestMapping("/goal")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.GOAL_API_TAG})
public class GoalsController {

  private final GoalService goalService;

  @GetMapping("/active")
  @ApiOperation(value = "List of all active Goals.")
  public List<GoalDto> listActive() {
    return goalService.listActive();
  }

  @GetMapping("/completed")
  @ApiOperation(value = "List of all active Goals.")
  public List<GoalDto> listCompleted() {
    return goalService.listCompleted();
  }

  @PostMapping()
  @ApiOperation(value = "Create new active Goal.")
  public GoalDto create(@RequestBody @Valid NewGoalDto newGoalDto, @ApiIgnore @AuthenticationPrincipal OidcUser user) {
    UserDto userDto = new UserInfoToDtoConverter().convert(user.getClaims());
    return goalService.create(newGoalDto, userDto);
  }

  @PutMapping("/complete/{id}")
  @ApiOperation(value = "Mark an active Goal as completed.")
  public ResponseEntity<Void> complete(@PathVariable @NotBlank String id,
      @RequestBody @Valid CompleteGoalRequestDto dto) {
    goalService.complete(id, dto);
    return ResponseEntity.noContent()
        .build();
  }

  @PutMapping("/remove/{id}")
  @ApiOperation(value = "Mark an active Goal as rejected.")
  public ResponseEntity<Void> remove(@PathVariable @NotBlank String id) {
    goalService.remove(id);
    return ResponseEntity.noContent()
        .build();
  }
}