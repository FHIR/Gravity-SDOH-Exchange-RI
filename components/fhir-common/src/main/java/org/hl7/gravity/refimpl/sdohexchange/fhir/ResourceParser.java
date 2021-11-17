package org.hl7.gravity.refimpl.sdohexchange.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResourceParser {

  private final IParser parser;

  @Autowired
  public ResourceParser(FhirContext fhirContext) {
    this.parser = fhirContext.newJsonParser();
  }

  public String parse(Resource resource) {
    Optional<Resource> optionalResource = Optional.ofNullable(resource);
    return optionalResource.map(parser::encodeResourceToString)
        .orElse(null);
  }

  public List<String> parse(List<Resource> resources) {
    return Optional.ofNullable(resources)
        .orElse(Collections.emptyList())
        .stream()
        .map(parser::encodeResourceToString)
        .collect(Collectors.toList());
  }
}