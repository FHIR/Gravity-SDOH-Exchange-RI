package org.hl7.gravity.refimpl.sdohexchange.dto.converter.bundle.response;

import org.hl7.fhir.r4.model.Procedure;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ProcedureResponseDto;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mykhailo Stefantsiv
 */
public class ProcedureToResponseDtoConverter implements Converter<Procedure, List<ProcedureResponseDto>> {

  @Override
  public List<ProcedureResponseDto> convert(Procedure procedure) {
    List<ProcedureResponseDto> result = new ArrayList<>();
    String procedureDisplay = procedure.getCode()
        .getCodingFirstRep()
        .getDisplay();
    result.add(new ProcedureResponseDto(procedure.getIdElement()
        .getIdPart(), procedureDisplay));
    return result;
  }
}
