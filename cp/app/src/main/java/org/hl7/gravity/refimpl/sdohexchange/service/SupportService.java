package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.gravity.refimpl.sdohexchange.codes.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.converter.OrganizationToDtoConverter;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OrganizationDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SupportService {

  private final IGenericClient cpClient;

  public List<OrganizationDto> listOrganizations() {
    Bundle organizationsBundle = cpClient.search()
        .forResource(Organization.class)
        .sort()
        .descending(Constants.PARAM_LASTUPDATED)
        .where(Organization.TYPE.exactly()
            .systemAndCode(OrganizationTypeCode.SYSTEM, OrganizationTypeCode.CBO.toCode()))
        .returnBundle(Bundle.class)
        .execute();
    return FhirUtil.getFromBundle(organizationsBundle, Organization.class)
        .stream()
        .map(org -> new OrganizationToDtoConverter().convert(org))
        .collect(Collectors.toList());
  }
}