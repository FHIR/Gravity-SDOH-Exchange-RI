package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.OrganizationDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

// TODO - Copy from EHR module. Refactor.
@Slf4j
public class OrganizationToDtoConverter implements Converter<Organization, OrganizationDto> {

  @Override
  public OrganizationDto convert(Organization org) {
    String orgId = org.getIdElement()
        .getIdPart();
    OrganizationDto orgDto = new OrganizationDto(orgId);
    orgDto.setName(org.getName());
    //We are interested only in CBO/CP types. Other are ignored.
    Coding coding = FhirUtil.findCoding(org.getType(), OrganizationTypeCode.SYSTEM);
    if (coding == null) {
      orgDto.getErrors()
          .add(String.format(
              "Organization with id '%s' has no coding with system '%s' within a 'type' property. Such organizations "
                  + "are " + "not expected in this context.", orgId, OrganizationTypeCode.SYSTEM));
    } else {
      try {
        orgDto.setType(OrganizationTypeCode.fromCode(coding.getCode()));
      } catch (FHIRException exc) {
        orgDto.getErrors()
            .add(String.format("OrganizationTypeCode code '%s' cannot be resolved for Organization with id '%s'.",
                coding.getCode(), orgId));
      }
    }
    return orgDto;
  }
}