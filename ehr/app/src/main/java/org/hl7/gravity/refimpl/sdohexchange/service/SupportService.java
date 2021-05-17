package org.hl7.gravity.refimpl.sdohexchange.service;

import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.ConditionRepository;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.GoalRepository;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.OrganizationRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.OrganizationToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.ConditionToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.GoalToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConditionDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.GoalDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OrganizationDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SupportService {

  private final SmartOnFhirContext smartOnFhirContext;
  private final GoalRepository goalRepository;
  private final ConditionRepository conditionRepository;
  private final OrganizationRepository organizationRepository;

  public List<ConditionDto> listConditions() {
    // TODO possibly support filtering only for problem-list-item, health-concern categories. Currently _filter is
    //  not supported by Logica sandbox.
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle bundle = conditionRepository.findByPatient(smartOnFhirContext.getPatient());
    return FhirUtil.getFromBundle(bundle, Condition.class)
        .stream()
        .map(condition -> new ConditionToDtoConverter().convert(condition))
        .collect(Collectors.toList());
  }

  public List<GoalDto> listGoals() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle bundle = goalRepository.findByPatient(smartOnFhirContext.getPatient());
    return FhirUtil.getFromBundle(bundle, Goal.class)
        .stream()
        .map(goal -> new GoalToDtoConverter().convert(goal))
        .collect(Collectors.toList());
  }

  public List<OrganizationDto> listOrganizations() {
    Bundle bundle = organizationRepository.findBySystemType(OrganizationTypeCode.SYSTEM);
    return FhirUtil.getFromBundle(bundle, Organization.class)
        .stream()
        .map(org -> new OrganizationToDtoConverter().convert(org))
        .collect(Collectors.toList());
  }
}