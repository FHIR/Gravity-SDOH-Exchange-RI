package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.UserInfoToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.NewProblemDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ProblemDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.service.ProblemService;
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
@RequestMapping("/problem")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.PROBLEM_API_TAG})
public class ProblemsController {

  private final ProblemService problemService;

  @GetMapping("/active")
  @ApiOperation(value = "List of all active Problems.")
  public List<ProblemDto> listActive() {
    return problemService.listActive();
  }

  @GetMapping("/closed")
  @ApiOperation(value = "List of all closed Problems.")
  public List<ProblemDto> listClosed() {
    return problemService.listClosed();
  }

  @PostMapping()
  @ApiOperation(value = "Create new active Problem.")
  public ProblemDto create(@RequestBody @Valid NewProblemDto newProblemDto,
      @ApiIgnore @AuthenticationPrincipal OidcUser user) {
    UserDto userDto = new UserInfoToDtoConverter().convert(user.getClaims());
    return problemService.create(newProblemDto, userDto);
  }

  @PutMapping("/close/{id}")
  @ApiOperation(value = "Mark an active Problem as closed.")
  public ResponseEntity<Void> resolve(@PathVariable @NotBlank String id) {
    problemService.close(id);
    return ResponseEntity.noContent()
        .build();
  }
}