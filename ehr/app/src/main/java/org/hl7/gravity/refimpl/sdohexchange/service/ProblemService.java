package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.HealthConcernBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ProblemDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCoreConditionCategory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ProblemService {

  private final SmartOnFhirContext smartOnFhirContext;
  private final SDOHMappings sdohMappings;
  private final IGenericClient ehrClient;

  public List<ProblemDto> listActive() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchProblemQuery(ConditionClinicalStatus.ACTIVE).include(
        Condition.INCLUDE_EVIDENCE_DETAIL)
        .include(Observation.INCLUDE_DERIVED_FROM.setRecurse(true))
        .returnBundle(Bundle.class)
        .execute();

    // Reuse a HealthConcernBundleToDtoConverter for now. In future, when the Problem logic is refined - we will have
    // a separate converter for that.
    return new HealthConcernBundleToDtoConverter().convert(responseBundle)
        .stream()
        .map(hc -> {
          ProblemDto problemDto = new ProblemDto();
          BeanUtils.copyProperties(hc, problemDto);
          return problemDto;
        })
        .collect(Collectors.toList());
  }

  public List<ProblemDto> listClosed() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchProblemQuery(ConditionClinicalStatus.RESOLVED).include(
        Condition.INCLUDE_EVIDENCE_DETAIL)
        .include(Observation.INCLUDE_DERIVED_FROM.setRecurse(true))
        .returnBundle(Bundle.class)
        .execute();

    // Reuse a HealthConcernBundleToDtoConverter for now. In future, when the Problem logic is refined - we will have
    // a separate converter for that.
    return new HealthConcernBundleToDtoConverter().convert(responseBundle)
        .stream()
        .map(hc -> {
          ProblemDto problemDto = new ProblemDto();
          BeanUtils.copyProperties(hc, problemDto);
          return problemDto;
        })
        .collect(Collectors.toList());
  }

  private IQuery<IBaseBundle> searchProblemQuery(ConditionClinicalStatus status) {
    return ehrClient.search()
        .forResource(Condition.class)
        .where(Condition.PATIENT.hasId(smartOnFhirContext.getPatient()))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.CONDITION))
        .where(Condition.CLINICAL_STATUS.exactly()
            .code(status.toCode()))
        .where(Condition.CATEGORY.exactly()
            .systemAndCode(UsCoreConditionCategory.PROBLEMLISTITEM.getSystem(),
                UsCoreConditionCategory.PROBLEMLISTITEM.toCode()));
  }
}