package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.TaskRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.TaskBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.TaskStatus;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.UpdateTaskRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCoreProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor.TaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskFailBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.TaskUpdateBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A Service to work with CP Tasks (usually retrieved from the EHR). It skips any Tasks created by this CP for the
 * different CBOs.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskService {

  private final IGenericClient cpClient;
  private final TaskRepository taskRepository;
  private final SDOHMappings sdohMappings;

  public List<TaskDto> readAll() {
    Bundle tasksBundle = taskRepository.findAllTasks();
    return new TaskBundleToDtoConverter().convert(tasksBundle);
  }

  public TaskDto read(String id) {
    Bundle taskBundle = taskRepository.find(id, Lists.newArrayList(Task.INCLUDE_FOCUS));
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
    Bundle taskBundle = taskRepository.find(id, Lists.newArrayList(Task.INCLUDE_FOCUS));
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
    bundleFactory.setPriorityForCBO(update.getPriorityForCBO());

    if (update.getStatus() == TaskStatus.ACCEPTED) {
      //TODO move role/org extraction to some Extractor class.
      bundleFactory.setCboTaskRequester(getRole(user).getOrganization());
      if (update.getCboPerformer() != null) {
        Organization cboPerformer = getCBOOrganization(update.getCboPerformer());
        bundleFactory.setCboTaskOwner(FhirUtil.toReference(Organization.class, cboPerformer.getIdElement()
            .getIdPart(), cboPerformer.getName()));
      }
    }
    taskRepository.transaction(bundleFactory.createUpdateBundle());
    // Usually these will be only the cancel statuses. If so - just cancel the subsequent (own) tasks.
    if (update.getStatus() != TaskStatus.ACCEPTED) {
      try {
        sync(taskInfo.getTask(), taskInfo.getServiceRequest());
      } catch (ResourceNotFoundException exc) {
        taskRepository.transaction(new TaskFailBundleFactory(taskInfo.getTask(), taskInfo.getServiceRequest(),
            exc.getMessage()).createFailBundle());
      }
    }
  }

  private Organization getCBOOrganization(String orgId) {
    Organization cboPerformer = cpClient.read()
        .resource(Organization.class)
        .withId(orgId)
        .execute();
    //TODO check org is CBO
    return cboPerformer;
  }

  private PractitionerRole getRole(UserDto user) {
    Bundle prs = cpClient.search()
        .forResource(PractitionerRole.class)
        .where(PractitionerRole.PRACTITIONER.hasId(user.getId()))
        .and(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(UsCoreProfiles.PRACTITIONER_ROLE))
        .and(new StringClientParam(PractitionerRole.SP_ORGANIZATION + "." + Constants.PARAM_PROFILE).matches()
            .value(UsCoreProfiles.ORGANIZATION))
        .returnBundle(Bundle.class)
        .execute();
    List<PractitionerRole> roles = FhirUtil.getFromBundle(prs, PractitionerRole.class);

    if (roles.isEmpty()) {
      throw new IllegalStateException(
          "No Practitioner role with US Core profile which references to US Core Organization have been found.");
    } else if (roles.size() > 1) {
      throw new IllegalStateException(
          "More than one Practitioner role with US Core profile which references to US Core Organization have been "
              + "found.");
    }
    return roles.stream()
        .findFirst()
        .get();
  }

  public void sync(Task task, final ServiceRequest serviceRequest) {
    Bundle ownUpdateBundle = new Bundle();
    ownUpdateBundle.setType(Bundle.BundleType.TRANSACTION);

    Bundle ownTaskBundle = taskRepository.findOurTask(task);
    TaskInfoHolder ownTaskInfo = new TaskInfoBundleExtractor().extract(ownTaskBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("No own task is found for task " + task.getIdElement()
            .getIdPart()));

    Task ownTask = ownTaskInfo.getTask();
    ownTask.setStatus(task.getStatus());
    ownTask.setStatusReason(task.getStatusReason());
    ownTask.setLastModifiedElement(task.getLastModifiedElement());
    ownTask.setNote(task.getNote());
    ownUpdateBundle.addEntry(FhirUtil.createPutEntry(ownTask));

    ServiceRequest cpServiceRequest = ownTaskInfo.getServiceRequest();
    if (!serviceRequest.getStatus()
        .equals(cpServiceRequest.getStatus())) {
      cpServiceRequest.setStatus(serviceRequest.getStatus());
      ownUpdateBundle.addEntry(FhirUtil.createPutEntry(cpServiceRequest));
    }
    taskRepository.transaction(ownUpdateBundle);
  }

}