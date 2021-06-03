package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.hl7.fhir.r4.model.codesystems.SearchModifierCode;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.TaskBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.UpdateTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskUpdateBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor.TaskInfoHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskService {

  private final IGenericClient cpClient;
  private final SDOHMappings sdohMappings;

  public List<TaskDto> readAll() {
    Bundle tasksBundle = cpClient.search()
        .forResource(Task.class)
        .where(new TokenClientParam(Task.SP_STATUS + ":" + SearchModifierCode.NOT.toCode()).exactly()
            .code(TaskStatus.REQUESTED.toCode()))
        // include ServiceRequest
        .include(Task.INCLUDE_FOCUS)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .returnBundle(Bundle.class)
        .execute();
    return new TaskBundleToDtoConverter().convert(tasksBundle);
  }

  public TaskDto read(String id) {
    Bundle taskBundle = cpClient.search()
        .forResource(Task.class)
        .where(Task.RES_ID.exactly()
            .code(id))
        // include ServiceRequest
        .include(Task.INCLUDE_FOCUS)
        .returnBundle(Bundle.class)
        .execute();
    return new TaskBundleToDtoConverter().convert(taskBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id)));
  }

  public void update(String id, UpdateTaskRequestDto update, UserDto user) {
    // Validates and converts Procedure codes to Coding
    List<Coding> procedureCodes = Optional.ofNullable(update.getProcedureCodes())
        .orElse(Collections.emptyList())
        .stream()
        .map(code -> sdohMappings.findResourceCoding(Procedure.class, code))
        .collect(Collectors.toList());
    Bundle taskBundle = cpClient.search()
        .forResource(Task.class)
        .where(Task.RES_ID.exactly()
            .code(id))
        .include(Task.INCLUDE_FOCUS)
        .returnBundle(Bundle.class)
        .execute();
    TaskInfoHolder taskInfo = new TaskInfoBundleExtractor().extract(taskBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id)));
    TaskUpdateBundleFactory bundleFactory = new TaskUpdateBundleFactory();
    bundleFactory.setTask(taskInfo.getTask());
    bundleFactory.setServiceRequest(taskInfo.getServiceRequest());
    bundleFactory.setStatus(update.getTaskStatus());
    bundleFactory.setStatusReason(update.getStatusReason());
    bundleFactory.setComment(update.getComment());
    bundleFactory.setOutcome(update.getOutcome());
    bundleFactory.setProcedureCodes(procedureCodes);
    bundleFactory.setUser(user);
    cpClient.transaction()
        .withBundle(bundleFactory.createUpdateBundle())
        .execute();
  }
}