package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.config.SpringFoxConfig;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.UserInfoToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.BaseConsentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConsentAttachmentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConsentDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.service.ConsentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author Mykhailo Stefantsiv
 */
@RestController
@RequestMapping("consent")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(tags = {SpringFoxConfig.CONSENT_API_TAG})
public class ConsentController {

  private final ConsentService consentService;

  @ApiOperation(value = "List of all Consent resources for logged in patient")
  @GetMapping
  public List<ConsentDto> listConsents() {
    return consentService.listConsents();
  }

  @ApiOperation(value = "List of all Consent resources base info (only id and a name) for logged in patient")
  @GetMapping("/list")
  public List<BaseConsentDto> listBaseConsentsInfo() {
    return consentService.listBaseConsentsInfo();
  }

  @ApiOperation(value = "Creates new Consent resource")
  @PostMapping
  public ConsentDto create(@RequestPart MultipartFile attachment, @RequestParam String name,
      @ApiIgnore @AuthenticationPrincipal OidcUser user) {
    UserDto userDto = new UserInfoToDtoConverter().convert(user.getClaims());
    return consentService.createConsent(name, attachment, userDto);
  }

  @ApiOperation(value = "Downloads a Consent attachment file")
  @GetMapping("/{id}/attachment")
  public ResponseEntity<byte[]> downloadAttachment(@PathVariable String id) {
    ConsentAttachmentDto attachment = consentService.retrieveAttachmentInfo(id);
    return ResponseEntity.ok()
        .header("Content-disposition", String.format("attachment; filename=%s.pdf", attachment.getTitle()))
        .contentType(MediaType.valueOf(attachment.getContentType()))
        .body(attachment.getContent());
  }
}
