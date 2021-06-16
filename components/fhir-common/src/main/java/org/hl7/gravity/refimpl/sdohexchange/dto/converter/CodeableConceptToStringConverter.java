package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

public class CodeableConceptToStringConverter implements Converter<CodeableConcept, String> {

  private final String delimiter;

  public CodeableConceptToStringConverter(String delimiter) {
    this.delimiter = delimiter;
  }

  @Override
  public String convert(CodeableConcept codeableConcept) {
    return StringUtils.hasText(codeableConcept.getText())
        ? codeableConcept.getText()
        : codeableConcept.getCoding()
            .stream()
            .map(Coding::getDisplay)
            .collect(Collectors.joining(delimiter));
  }
}
