package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.google.common.collect.Lists;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.auth.AuthorizationClient;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.dao.ServerRepository;
import org.hl7.gravity.refimpl.sdohexchange.dao.TaskRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.TaskBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.UpdateTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.AuthClientException;
import org.hl7.gravity.refimpl.sdohexchange.exception.ServerNotFoundException;
import org.hl7.gravity.refimpl.sdohexchange.exception.TaskReadException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskUpdateBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.model.Server;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

  private static final String SCOPE = "address phone read profile openid email write";

  @Value("${app.url}")
  private String applicationUrl;

  private final FhirContext fhirContext;
  private final ServerRepository serverRepository;
  private final SDOHMappings sdohMappings;
  private final AuthorizationClient authorizationClient;

  public TaskService(ServerRepository serverRepository, FhirContext fhirContext, SDOHMappings sdohMappings) {
    this.fhirContext = fhirContext;
    this.serverRepository = serverRepository;
    this.sdohMappings = sdohMappings;
    this.authorizationClient = new AuthorizationClient(new RestTemplate());
  }

  public List<TaskDto> readAll() throws AuthClientException {
    List<Server> serverList = serverRepository.findAll();
    List<TaskDto> taskDtoList = new ArrayList<>();
    for (Server server : serverList) {
      IGenericClient fhirClient = fhirContext.newRestfulGenericClient(server.getFhirServerUrl());
      // Doesn't support now
      //      fhirClient.registerInterceptor(new BearerTokenAuthInterceptor(
      //          authorizationClient.getTokenResponse(URI.create(server.getAuthServerUrl()), server.getClientId(),
      //                  server.getClientSecret(), SCOPE)
      //              .getAccessToken()));
      TaskRepository taskRepository = new TaskRepository(fhirClient, applicationUrl);
      taskDtoList.addAll(new TaskBundleToDtoConverter(server.getId()).convert(taskRepository.findAllTasks()));
    }
    return taskDtoList;
  }

  public TaskDto read(Integer serverId, String taskId) {
    Server server = serverRepository.findById(serverId)
        .orElseThrow(() -> new ServerNotFoundException(String.format("No server was found by id '%s'", serverId)));
    IGenericClient fhirClient = fhirContext.newRestfulGenericClient(server.getFhirServerUrl());
    // Doesn't support now
    //      fhirClient.registerInterceptor(new BearerTokenAuthInterceptor(
    //          authorizationClient.getTokenResponse(URI.create(server.getAuthServerUrl()), server.getClientId(),
    //                  server.getClientSecret(), SCOPE)
    //              .getAccessToken()));
    TaskRepository taskRepository = new TaskRepository(fhirClient, applicationUrl);
    Bundle taskBundle = taskRepository.find(taskId, Lists.newArrayList(Task.INCLUDE_FOCUS));
    Task task = FhirUtil.getFirstFromBundle(taskBundle, Task.class);
    if (Objects.isNull(task)) {
      throw new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), taskId));
    }
    if (!task.getIntent()
        .equals(Task.TaskIntent.FILLERORDER)) {
      throw new TaskReadException("The intent of Task/" + taskId + " is not filler-order.");
    }
    return new TaskBundleToDtoConverter(serverId).convert(taskBundle)
        .stream()
        .findFirst()
        .get();
  }

  public void update(String id, UpdateTaskRequestDto update) throws AuthClientException {
    Server server = serverRepository.findById(update.getServerId())
        .orElseThrow(
            () -> new ServerNotFoundException(String.format("No server was found by id '%s'", update.getServerId())));
    IGenericClient fhirClient = fhirContext.newRestfulGenericClient(server.getFhirServerUrl());
    // Doesn't support now
    //      fhirClient.registerInterceptor(new BearerTokenAuthInterceptor(
    //          authorizationClient.getTokenResponse(URI.create(server.getAuthServerUrl()), server.getClientId(),
    //                  server.getClientSecret(), SCOPE)
    //              .getAccessToken()));
    TaskRepository taskRepository = new TaskRepository(fhirClient, applicationUrl);

    // Validates and converts Procedure codes to Coding
    List<Coding> procedureCodes = Optional.ofNullable(update.getProcedureCodes())
        .orElse(Collections.emptyList())
        .stream()
        .map(code -> sdohMappings.findResourceCoding(Procedure.class, code))
        .collect(Collectors.toList());
    Bundle taskBundle = taskRepository.find(id, Lists.newArrayList(Task.INCLUDE_FOCUS));
    TaskInfoBundleExtractor.TaskInfoHolder taskInfo = new TaskInfoBundleExtractor().extract(taskBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id)));
    TaskUpdateBundleFactory bundleFactory = new TaskUpdateBundleFactory();
    bundleFactory.setTask(taskInfo.getTask());
    bundleFactory.setStatus(update.getTaskStatus());
    bundleFactory.setServiceRequest(taskInfo.getServiceRequest());
    bundleFactory.setStatusReason(update.getStatusReason());
    bundleFactory.setComment(update.getComment());
    bundleFactory.setOutcome(update.getOutcome());
    bundleFactory.setProcedureCodes(procedureCodes);

    taskRepository.transaction(bundleFactory.createUpdateBundle());
  }
}
