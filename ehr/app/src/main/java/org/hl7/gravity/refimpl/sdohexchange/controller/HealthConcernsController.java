package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.UserInfoToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.NewHealthConcernDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.HealthConcernDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.service.HealthConcernService;
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
@RequestMapping("/health-concern")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.HEALTH_CONCERN_API_TAG})
public class HealthConcernsController {

  private final HealthConcernService healthConcernService;

  @GetMapping("/active")
  @ApiOperation(value = "List of all active Health Concerns.")
  public List<HealthConcernDto> listActive() {
    return healthConcernService.listActive();
  }

  @GetMapping("/resolved")
  @ApiOperation(value = "List of all resolved Health Concerns.")
  public List<HealthConcernDto> listResolved() {
    return healthConcernService.listResolved();
  }

  @PostMapping()
  @ApiOperation(value = "Create new active Health Concern.")
  public HealthConcernDto create(@RequestBody @Valid NewHealthConcernDto newHealthConcernDto,
      @ApiIgnore @AuthenticationPrincipal OidcUser user) {
    UserDto userDto = new UserInfoToDtoConverter().convert(user.getClaims());
    return healthConcernService.create(newHealthConcernDto, userDto);
  }

  @PutMapping("/promote/{id}")
  @ApiOperation(value = "Promote an active Health Concern to a Problem.")
  public ResponseEntity<Void> promote(@PathVariable @NotBlank String id) {
    healthConcernService.promote(id);
    return ResponseEntity.noContent()
        .build();
  }

  @PutMapping("/resolve/{id}")
  @ApiOperation(value = "Mark an active Health Concern as resolved.")
  public ResponseEntity<Void> resolve(@PathVariable @NotBlank String id) {
    healthConcernService.resolve(id);
    return ResponseEntity.noContent()
        .build();
  }

  @PutMapping("/remove/{id}")
  @ApiOperation(value = "Mark an active Health Concern as inactive.")
  public ResponseEntity<Void> remove(@PathVariable @NotBlank String id) {
    healthConcernService.remove(id);
    return ResponseEntity.noContent()
        .build();
  }
}