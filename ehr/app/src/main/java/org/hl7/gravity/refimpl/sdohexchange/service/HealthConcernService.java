package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHMappings;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.System;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.HealthConcernBundleToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.NewHealthConcernDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.HealthConcernDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.exception.HealthConcernCreateException;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCoreConditionCategory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.HealthConcernInfoBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.HealthConcernPrepareBundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.HealthConcernPrepareBundleExtractor.HealthConcernPrepareInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.HealthConcernBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.fhir.factory.HealthConcernPrepareBundleFactory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class HealthConcernService {

  private final SmartOnFhirContext smartOnFhirContext;
  private final SDOHMappings sdohMappings;
  private final IGenericClient ehrClient;

  public List<HealthConcernDto> listActive() {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchHealthConcernQuery()
        .include(Condition.INCLUDE_EVIDENCE_DETAIL)
        .include(Observation.INCLUDE_DERIVED_FROM.setRecurse(true))
        .returnBundle(Bundle.class)
        .execute();
    return new HealthConcernBundleToDtoConverter().convert(responseBundle);
  }

  public HealthConcernDto create(NewHealthConcernDto newHealthConcernDto, UserDto user) {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    HealthConcernPrepareBundleFactory healthConcernPrepareBundleFactory =
        new HealthConcernPrepareBundleFactory(smartOnFhirContext.getPatient(), user.getId());
    Bundle healthConcernRelatedResources = ehrClient.transaction()
        .withBundle(healthConcernPrepareBundleFactory.createPrepareBundle())
        .execute();
    HealthConcernPrepareInfoHolder healthConcernPrepareInfoHolder =
        new HealthConcernPrepareBundleExtractor().extract(healthConcernRelatedResources);

    HealthConcernBundleFactory bundleFactory = new HealthConcernBundleFactory();
    bundleFactory.setName(newHealthConcernDto.getName());
    String category = newHealthConcernDto.getCategory();
    bundleFactory.setCategory(sdohMappings.findCategoryCoding(category));
    bundleFactory.setIcdCode(sdohMappings.findCoding(category, Condition.class, System.ICD_10,
        newHealthConcernDto.getIcdCode()));
    bundleFactory.setSnomedCode(sdohMappings.findCoding(category, Condition.class, System.SNOMED,
        newHealthConcernDto.getSnomedCode()));
    bundleFactory.setPatient(healthConcernPrepareInfoHolder.getPatient());
    bundleFactory.setPractitioner(healthConcernPrepareInfoHolder.getPractitioner());

    Bundle healthConcernCreateBundle = ehrClient.transaction()
        .withBundle(bundleFactory.createBundle())
        .execute();

    IdType healthConcernId = FhirUtil.getFromResponseBundle(healthConcernCreateBundle, Condition.class);
    Bundle responseBundle = searchHealthConcernQuery()
        .where(Condition.RES_ID.exactly()
            .code(healthConcernId.getIdPart()))
        .returnBundle(Bundle.class)
        .execute();
    return new HealthConcernBundleToDtoConverter().convert(responseBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new HealthConcernCreateException("Health Concern is not found in the response bundle."));
  }

  public void promote(String id) {
    Assert.notNull(smartOnFhirContext.getPatient(), "Patient id cannot be null.");

    Bundle responseBundle = searchHealthConcernQuery()
        .where(Condition.RES_ID.exactly()
            .code(id))
        .returnBundle(Bundle.class)
        .execute();
    Condition healthConcern = new HealthConcernInfoBundleExtractor().extract(responseBundle)
        .stream()
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(new IdType(Condition.class.getSimpleName(), id)))
        .getCondition();

    UsCoreConditionCategory problem = UsCoreConditionCategory.PROBLEMLISTITEM;
    Coding coding = FhirUtil.findCoding(healthConcern.getCategory(), problem.getSystem());
    coding.setCode(problem.toCode());
    coding.setDisplay(problem.getDisplay());
    healthConcern.setOnset(DateTimeType.now());

    ehrClient.update()
        .resource(healthConcern)
        .execute();
  }

  private IQuery<IBaseBundle> searchHealthConcernQuery() {
    return ehrClient.search()
        .forResource(Condition.class)
        .where(Condition.PATIENT.hasId(smartOnFhirContext.getPatient()))
        .where(new StringClientParam(Constants.PARAM_PROFILE).matches()
            .value(SDOHProfiles.CONDITION))
        .where(Condition.CLINICAL_STATUS.exactly()
            .code(ConditionClinicalStatus.ACTIVE.toCode()))
        .where(Condition.CATEGORY.exactly()
            .systemAndCode(UsCoreConditionCategory.HEALTHCONCERN.getSystem(),
                UsCoreConditionCategory.HEALTHCONCERN.toCode()));
  }
}