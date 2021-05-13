package org.hl7.gravity.refimpl.sdohexchange.dto.converter.info;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Consent;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.ConditionToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.GoalToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConsentResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OccurrenceResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ServiceRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.info.ServiceRequestInfo;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.stream.Collectors;

@Slf4j
public class ServiceRequestInfoToDtoConverter implements Converter<ServiceRequestInfo, ServiceRequestDto> {

  private final GoalToDtoConverter goalToDtoConverter;
  private final ConditionToDtoConverter conditionToDtoConverter;

  public ServiceRequestInfoToDtoConverter() {
    this.goalToDtoConverter = new GoalToDtoConverter();
    this.conditionToDtoConverter = new ConditionToDtoConverter();
  }

  @Override
  public ServiceRequestDto convert(ServiceRequestInfo serviceRequestInfo) {
    ServiceRequest serviceRequest = serviceRequestInfo.getServiceRequest();
    String id = serviceRequest.getIdElement()
        .getIdPart();
    ServiceRequestDto serviceRequestDto = new ServiceRequestDto(id);
    Coding categoryCode = serviceRequest.getCategoryFirstRep()
        .getCodingFirstRep();
    serviceRequestDto.setCategory(new CodingDto(categoryCode.getCode(), categoryCode.getDisplay()));
    Coding requestCode = serviceRequest.getCode()
        .getCodingFirstRep();
    serviceRequestDto.setCode(new CodingDto(requestCode.getCode(), requestCode.getDisplay()));
    serviceRequestDto.setOccurrence(convertOccurrence(serviceRequest.getOccurrence()));

    serviceRequestDto.setGoals(serviceRequestInfo.getGoals()
        .stream()
        .map(goalToDtoConverter::convert)
        .collect(Collectors.toList()));
    serviceRequestDto.setConditions(serviceRequestInfo.getConditions()
        .stream()
        .map(conditionToDtoConverter::convert)
        .collect(Collectors.toList()));
    //TODO: confirm display
    Consent consent = serviceRequestInfo.getConsent();
    ConsentResponseDto consentResponseDto = new ConsentResponseDto(consent.getIdElement()
        .getIdPart(), consent.getScope()
        .getCodingFirstRep()
        .getDisplay());
    serviceRequestDto.setConsent(consentResponseDto);
    return serviceRequestDto;
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