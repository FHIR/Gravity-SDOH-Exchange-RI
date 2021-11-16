package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TaskJsonResourcesDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.ResourceLoader;
import org.hl7.gravity.refimpl.sdohexchange.fhir.ResourceParser;
import org.hl7.gravity.refimpl.sdohexchange.fhir.reference.util.ServiceRequestReferenceCollector;
import org.hl7.gravity.refimpl.sdohexchange.fhir.reference.util.TaskReferenceCollector;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ResourceService {

  private final IGenericClient cpClient;
  private final ResourceLoader resourceLoader;
  private final ResourceParser resourceParser;

  public TaskJsonResourcesDto getTaskResources(String id) {
    // Getting task by id with Patient, requester Organization and ServiceRequest
    Bundle taskBundle = cpClient.search()
        .forResource(Task.class)
        .where(Task.RES_ID.exactly()
            .code(id))
        .include(Task.INCLUDE_FOCUS)
        .include(Task.INCLUDE_PATIENT)
        .include(Task.INCLUDE_REQUESTER)
        .returnBundle(Bundle.class)
        .execute();

    Task task = FhirUtil.getFromBundle(taskBundle, Task.class)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Task.class.getSimpleName(), id)));
    // We expect that task was validated and contains all resources
    ServiceRequest serviceRequest = FhirUtil.getFirstFromBundle(taskBundle, ServiceRequest.class);
    Patient patient = FhirUtil.getFirstFromBundle(taskBundle, Patient.class);
    Organization requester = FhirUtil.getFirstFromBundle(taskBundle, Organization.class);

    // Load all Task Procedures and ServiceRequest required resources as one transaction
    Map<Class<? extends Resource>, List<Resource>> loadedResources = resourceLoader.getResources(cpClient,
        collectAllReferences(task, serviceRequest));

    TaskJsonResourcesDto resourcesDto = new TaskJsonResourcesDto();
    resourcesDto.setTask(resourceParser.parse(task));
    resourcesDto.setServiceRequest(resourceParser.parse(serviceRequest));
    resourcesDto.setPatient(resourceParser.parse(patient));
    resourcesDto.setRequester(resourceParser.parse(requester));
    resourcesDto.setConsent(resourceParser.parse(loadedResources.get(Consent.class))
        .stream()
        .findFirst()
        .orElse(null));
    resourcesDto.setConditions(resourceParser.parse(loadedResources.get(Condition.class)));
    resourcesDto.setGoals(resourceParser.parse(loadedResources.get(Goal.class)));
    resourcesDto.setProcedures(resourceParser.parse(loadedResources.get(Procedure.class)));
    return resourcesDto;
  }

  private List<Reference> collectAllReferences(Task task, ServiceRequest serviceRequest) {
    List<Reference> references = new ArrayList<>();
    references.addAll(TaskReferenceCollector.getOutputProcedures(task));
    references.addAll(ServiceRequestReferenceCollector.getConsents(serviceRequest));
    references.addAll(ServiceRequestReferenceCollector.getGoals(serviceRequest));
    references.addAll(ServiceRequestReferenceCollector.getConditions(serviceRequest));
    return references;
  }
}