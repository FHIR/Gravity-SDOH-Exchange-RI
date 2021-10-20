package org.hl7.gravity.refimpl.sdohexchange.service;

import org.hl7.gravity.refimpl.sdohexchange.auth.AuthorizationClient;
import org.hl7.gravity.refimpl.sdohexchange.dao.ServerRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.NewServerDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ServerDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.AuthClientException;
import org.hl7.gravity.refimpl.sdohexchange.exception.DuplicateServerNameNotAllowedException;
import org.hl7.gravity.refimpl.sdohexchange.exception.ServerNotFoundException;
import org.hl7.gravity.refimpl.sdohexchange.model.Server;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ServerService {

  private static final String SCOPE = "address phone read profile openid email write";

  private final ServerRepository serverRepository;
  private final AuthorizationClient authorizationClient;
  private final ModelMapper modelMapper;

  public ServerService(ServerRepository serverRepository) {
    this.serverRepository = serverRepository;
    this.authorizationClient = new AuthorizationClient(new RestTemplate());
    this.modelMapper = new ModelMapper();
  }

  public ServerDto getServer(Integer id) {
    Server server = serverRepository.findById(id)
        .orElseThrow(() -> new ServerNotFoundException(String.format("No server was found by id '%s'", id)));
    return modelMapper.map(server, ServerDto.class);
  }

  public List<ServerDto> getServers() {
    return serverRepository.findAll()
        .stream()
        .map(obj -> modelMapper.map(obj, ServerDto.class))
        .collect(Collectors.toList());
  }

  @Transactional
  public ServerDto createServer(NewServerDto newServerDto) throws AuthClientException {
    if (serverRepository.findByServerName(newServerDto.getServerName())
        .isPresent()) {
      throw new DuplicateServerNameNotAllowedException(newServerDto.getServerName());
    }
    Server server = modelMapper.map(newServerDto, Server.class);
    // Just a validation of credentials.
    authorizationClient.getTokenResponse(URI.create(newServerDto.getAuthServerUrl()), newServerDto.getClientId(),
        newServerDto.getClientSecret(), SCOPE);
    serverRepository.save(server);
    return modelMapper.map(server, ServerDto.class);
  }

  @Transactional
  public ServerDto updateServer(Integer id, NewServerDto newServerDto) throws AuthClientException {
    Server server = serverRepository.findById(id)
        .orElseThrow(() -> new ServerNotFoundException(String.format("No Server was found by id '%s'", id)));
    if (serverRepository.findByServerName(newServerDto.getServerName())
        .isPresent() && !server.getServerName()
        .equals(newServerDto.getServerName())) {
      throw new DuplicateServerNameNotAllowedException(newServerDto.getServerName());
    }
    // Just a validation of credentials.
    authorizationClient.getTokenResponse(URI.create(newServerDto.getAuthServerUrl()), newServerDto.getClientId(),
        newServerDto.getClientSecret(), SCOPE);
    modelMapper.map(newServerDto, server);
    return modelMapper.map(server, ServerDto.class);
  }

  @Transactional
  public void deleteServer(Integer id) {
    if (serverRepository.deleteServerById(id) == 0) {
      throw new ServerNotFoundException(String.format("No Server was found by id '%s'", id));
    }
  }
}
