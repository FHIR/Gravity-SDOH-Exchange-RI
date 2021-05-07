package org.hl7.gravity.refimpl.sdohexchange.dto.converter.bundle.response;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ProcedureResponseDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mykhailo Stefantsiv
 */
public class ProcedureBundleToResponseDtoConverter implements Converter<Bundle, List<ProcedureResponseDto>> {

  @Override
  public List<ProcedureResponseDto> convert(Bundle bundle) {
    List<ProcedureResponseDto> result = new ArrayList<>();
    for (Procedure procedure : FhirUtil.getFromBundle(bundle, Procedure.class)) {
      String procedureDisplay = procedure.getCode().getCodingFirstRep().getDisplay();
      result.add(new ProcedureResponseDto(procedure.getIdElement().getIdPart(), procedureDisplay));
    }
    return result;
  }
}
