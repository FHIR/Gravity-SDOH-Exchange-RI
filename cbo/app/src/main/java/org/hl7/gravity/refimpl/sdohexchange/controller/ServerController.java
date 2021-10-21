package org.hl7.gravity.refimpl.sdohexchange.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.NewServerDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ServerDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.AuthClientException;
import org.hl7.gravity.refimpl.sdohexchange.service.ServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("servers")
@Api(tags = "Server API")
public class ServerController {

  private final ServerService serverService;

  @GetMapping("/{id}")
  @ApiOperation(value = "Get server by id")
  public ServerDto read(@PathVariable("id") Integer id) {
    return serverService.getServer(id);
  }

  @GetMapping
  @ApiOperation(value = "List of all servers")
  public List<ServerDto> list() {
    return serverService.getServers();
  }

  @PostMapping
  @ApiOperation(value = "Add new server")
  public ServerDto create(@RequestBody @Valid NewServerDto newServerDto) throws AuthClientException {
    return serverService.createServer(newServerDto);
  }

  @PutMapping("/{id}")
  @ApiOperation(value = "Update server")
  public ServerDto update(@PathVariable("id") Integer id, @RequestBody @Valid NewServerDto newServerDto)
      throws AuthClientException {
    return serverService.updateServer(id, newServerDto);
  }

  @DeleteMapping("/{id}")
  @ApiOperation(value = "Remove server")
  public ResponseEntity<Void> remove(@PathVariable("id") Integer id) {
    serverService.deleteServer(id);
    return ResponseEntity.noContent()
        .build();
  }
}
