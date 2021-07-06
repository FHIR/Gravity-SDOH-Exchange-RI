package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Consent;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ConsentDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Mykhailo Stefantsiv
 */
public class ConsentToDtoConverter implements Converter<Consent, ConsentDto> {

  @Override
  public ConsentDto convert(Consent consent) {
    return ConsentDto.builder()
        .id(consent.getIdElement().getIdPart())
        .name(consent.getSourceAttachment().getTitle())
        .category(consent.getCategoryFirstRep().getCodingFirstRep().getCode())
        .consentDate(FhirUtil.toLocalDateTime(consent.getDateTimeElement()))
        .scope(consent.getScope().getCodingFirstRep().getDisplay())
        .status(consent.getStatus().getDisplay())
        .organization(consent.getOrganizationFirstRep().getDisplay())
        .build();
  }
}
