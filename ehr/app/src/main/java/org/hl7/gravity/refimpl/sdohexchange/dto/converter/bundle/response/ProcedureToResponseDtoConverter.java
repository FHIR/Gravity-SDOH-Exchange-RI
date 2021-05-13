package org.hl7.gravity.refimpl.sdohexchange.dto.converter.bundle.response;

import org.hl7.fhir.r4.model.Procedure;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ProcedureResponseDto;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Mykhailo Stefantsiv
 */
public class ProcedureToResponseDtoConverter implements Converter<Procedure, ProcedureResponseDto> {

  @Override
  public ProcedureResponseDto convert(Procedure procedure) {
    String procedureDisplay = procedure.getCode()
        .getCodingFirstRep()
        .getDisplay();
    return new ProcedureResponseDto(procedure.getIdElement()
        .getIdPart(), procedureDisplay);
  }
}
