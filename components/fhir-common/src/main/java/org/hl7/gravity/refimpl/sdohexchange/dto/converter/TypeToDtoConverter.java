package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.StringTypeDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.TypeDto;
import org.springframework.core.convert.converter.Converter;

public class TypeToDtoConverter implements Converter<Type, TypeDto> {

  private final CodeableConceptToStringConverter codeableConceptToStringConverter =
      new CodeableConceptToStringConverter(", ");

  @Override
  public TypeDto convert(Type type) {
    if (type instanceof StringType) {
      return new StringTypeDto(((StringType) type).getValue());
    } else if (type instanceof Reference) {
      IIdType element = ((Reference) type).getReferenceElement();
      return new ReferenceDto(element.getIdPart(), ((Reference) type).getDisplay());
    } else if (type instanceof Coding) {
      return new StringTypeDto(((Coding) type).getDisplay());
    } else if (type instanceof CodeableConcept) {
      return new StringTypeDto(codeableConceptToStringConverter.convert((CodeableConcept) type));
    }
    return null;
  }
}
