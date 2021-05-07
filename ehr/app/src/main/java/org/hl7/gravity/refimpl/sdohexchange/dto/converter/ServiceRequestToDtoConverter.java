package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.RequestCode;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHDomainCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.bundle.response.ConditionBundleToResponseDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.bundle.response.GoalBundleToResponseDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConsentResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OccurrenceResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ServiceRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.util.ReferenceUtil;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

@Slf4j
public class ServiceRequestToDtoConverter implements Converter<ServiceRequest, ServiceRequestDto> {

  private final IGenericClient ehrClient;
  private final GoalBundleToResponseDtoConverter goalBundleToDtoConverter;
  private final ConditionBundleToResponseDtoConverter conditionBundleToDtoConverter;

  public ServiceRequestToDtoConverter(IGenericClient ehrClient) {
    this.ehrClient = ehrClient;
    this.goalBundleToDtoConverter = new GoalBundleToResponseDtoConverter();
    this.conditionBundleToDtoConverter = new ConditionBundleToResponseDtoConverter();
  }

  @Override
  public ServiceRequestDto convert(ServiceRequest serviceRequest) {
    String id = serviceRequest.getIdElement().getIdPart();
    ServiceRequestDto serviceRequestDto = new ServiceRequestDto(id);
    setCategory(serviceRequest, serviceRequestDto);
    String code = serviceRequest.getCode().getCodingFirstRep().getCode();
    serviceRequestDto.setRequestCode(RequestCode.fromCode(code).getDisplay());
    serviceRequestDto.setOccurrence(convertOccurrence(serviceRequest.getOccurrence()));

    List<String> goals = ReferenceUtil.retrieveReferencedIds(serviceRequest.getSupportingInfo(), Goal.class);
    List<String> conditions = ReferenceUtil.retrieveReferencedIds(serviceRequest.getReasonReference(), Condition.class);
    Bundle goalsBundle = ehrClient.search()
        .forResource(Goal.class)
        .where(Goal.RES_ID.exactly().codes(goals))
        .returnBundle(Bundle.class)
        .execute();
    Bundle conditionsBundle = ehrClient.search()
        .forResource(Condition.class)
        .where(Condition.RES_ID.exactly().codes(conditions))
        .returnBundle(Bundle.class)
        .execute();
    serviceRequestDto.setGoals(goalBundleToDtoConverter.convert(goalsBundle));
    serviceRequestDto.setConditions(conditionBundleToDtoConverter.convert(conditionsBundle));
    String consentId = ReferenceUtil.retrieveReferencedIds(serviceRequest.getSupportingInfo(), Consent.class).get(0);
    Consent consent = ehrClient.read().resource(Consent.class).withId(consentId).execute();
    //TODO: confirm display
    ConsentResponseDto consentResponseDto = new ConsentResponseDto(consent.getIdElement()
        .getIdPart(), consent.getScope().getCodingFirstRep().getDisplay());
    serviceRequestDto.setConsent(consentResponseDto);
    return serviceRequestDto;
  }

  private void setCategory(ServiceRequest serviceRequest, ServiceRequestDto serviceRequestDto) {
    // TODO validate profile and other properties using InstanceValidator
    //We are interested only in a single SDOHDomainCode category. Other are ignored.
    Coding coding = FhirUtil.findCoding(serviceRequest.getCategory(), SDOHDomainCode.SYSTEM);
    if (coding == null) {
      serviceRequestDto.getErrors()
          .add(String.format(
              "ServiceRequest with id '%s' has no category with system '%s' within a 'category' property. Such "
                  + "requests are not expected in this context.", serviceRequestDto.getId(), SDOHDomainCode.SYSTEM));
    } else {
      try {
        serviceRequestDto.setCategory(SDOHDomainCode.fromCode(coding.getCode())
            .getDisplay());
      } catch (FHIRException exc) {
        serviceRequestDto.getErrors()
            .add(String.format("SDOHDomainCode code '%s' cannot be resolved for ServiceRequest with id '%s'.",
                coding.getCode(), serviceRequestDto.getId()));
      }
    }
  }

  private OccurrenceResponseDto convertOccurrence(Type occurrence) {
    OccurrenceResponseDto occurrenceResponseDto = null;
    if (occurrence instanceof DateTimeType) {
      occurrenceResponseDto = new OccurrenceResponseDto(null, FhirUtil.toLocalDateTime((DateTimeType) occurrence));
    } else if (occurrence instanceof Period) {
      occurrenceResponseDto = new OccurrenceResponseDto(
          FhirUtil.toLocalDateTime(((Period) occurrence).getStartElement()),
          FhirUtil.toLocalDateTime(((Period) occurrence).getEndElement()));
    }
    return occurrenceResponseDto;
  }

}