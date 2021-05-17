package org.hl7.gravity.refimpl.sdohexchange.info.composer;

import ca.uhn.fhir.context.FhirContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.ServiceRequestRepository;
import org.hl7.gravity.refimpl.sdohexchange.info.ServiceRequestInfo;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ServiceRequestInfoComposer {

  private final FhirContext fhirContext;
  private final ServiceRequestRepository serviceRequestRepository;

  public Map<String, ServiceRequestInfo> compose(Collection<ServiceRequest> serviceRequests) {
    Set<String> referenceUrls = serviceRequests.stream()
        .map(serviceRequest ->
            FhirUtil.getReferences(fhirContext, serviceRequest,
                Arrays.asList(Consent.class, Goal.class,
                    Condition.class))
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(this::toResourcePath)
                .collect(Collectors.toSet())
        )
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());

    Bundle loadResourcesBundle = new Bundle();
    loadResourcesBundle.setType(Bundle.BundleType.TRANSACTION);
    for (String referenceUrl : referenceUrls) {
      loadResourcesBundle.addEntry()
          .getRequest()
          .setMethod(Bundle.HTTPVerb.GET)
          .setUrl(referenceUrl);
    }
    Bundle loadedResourcesBundle = serviceRequestRepository.transaction(loadResourcesBundle);
    Map<String, Resource> serviceRequestsResources = loadedResourcesBundle.getEntry()
        .stream()
        .map(BundleEntryComponent::getResource)
        .collect(Collectors.toMap(resource -> resource.getResourceType()
            .toString() + "/" + resource.getIdElement()
            .getIdPart(), Function.identity()));

    return serviceRequests.stream()
        .collect(Collectors.toMap(serviceRequest -> serviceRequest.getIdElement()
            .getIdPart(), serviceRequest -> {
          Map<String, List<Reference>> serviceRequestReferences = FhirUtil.getReferences(fhirContext, serviceRequest,
              Arrays.asList(Consent.class, Goal.class,
                  Condition.class));
          Reference consentRef = serviceRequestReferences.get(Consent.class.getSimpleName())
              .get(0);
          Consent consent = (Consent) serviceRequestsResources.get(toResourcePath(consentRef));
          List<Goal> goals = serviceRequestReferences.get(Goal.class.getSimpleName())
              .stream()
              .map(this::toResourcePath)
              .map(serviceRequestsResources::get)
              .map(Goal.class::cast)
              .collect(Collectors.toList());
          List<Condition> conditions = serviceRequestReferences.get(Condition.class.getSimpleName())
              .stream()
              .map(this::toResourcePath)
              .map(serviceRequestsResources::get)
              .map(Condition.class::cast)
              .collect(Collectors.toList());
          return new ServiceRequestInfo(serviceRequest, consent, goals, conditions);
        }));
  }

  private String toResourcePath(Reference reference) {
    IIdType idType = reference.getReferenceElement();
    return idType.getResourceType() + "/" + idType.getIdPart();
  }
}
