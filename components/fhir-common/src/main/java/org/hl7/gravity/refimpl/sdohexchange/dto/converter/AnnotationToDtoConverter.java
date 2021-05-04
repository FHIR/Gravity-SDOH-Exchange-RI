package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import org.hl7.fhir.r4.model.Annotation;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CommentDto;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

public class AnnotationToDtoConverter implements Converter<Annotation, CommentDto> {

  private final TypeToDtoConverter typeToDtoConverter = new TypeToDtoConverter();

  @Override
  public CommentDto convert(Annotation note) {
    return new CommentDto(typeToDtoConverter.convert(note.getAuthor()), FhirUtil.toLocalDateTime(note.getTimeElement()),
        note.getText());
  }
}