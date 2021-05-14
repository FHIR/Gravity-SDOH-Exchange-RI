package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StructureMap;
import org.hl7.fhir.r5.elementmodel.Element;
import org.hl7.fhir.r5.elementmodel.JsonParser;
import org.hl7.fhir.r5.elementmodel.Manager;
import org.hl7.fhir.r5.model.FhirPublication;
import org.hl7.fhir.utilities.TimeTracker;
import org.hl7.fhir.utilities.VersionUtilities;
import org.hl7.fhir.validation.ValidationEngine;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.QuestionnaireRepository;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.StructureMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class ConvertService {

  private static final String PACKAGE_VERSION = "4.0.1";
  private static final String SDOH_CLINICAL_CARE_VERSION = "0.1.0";
  private static final String SDOH_CLINICAL_CARE_PACKAGE = "hl7.fhir.us.sdoh-clinicalcare";
  private static final String CUSTOM_STRUCTURE_DEFINITIONS_LOCATION = "implementaion-guides";
  private static final String MAP_EXTENSION =
      "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap";

  private final IParser resourceParser;
  private final ValidationEngine validationEngine;
  private final QuestionnaireRepository questionnaireRepository;
  private final StructureMapRepository structureMapRepository;

  @Autowired
  public ConvertService(FhirContext fhirContext, QuestionnaireRepository questionnaireRepository,
      StructureMapRepository structureMapRepository) throws IOException, URISyntaxException {
    this.resourceParser = fhirContext.newJsonParser();
    this.questionnaireRepository = questionnaireRepository;
    this.structureMapRepository = structureMapRepository;
    String definitions = VersionUtilities.packageForVersion(PACKAGE_VERSION) + "#" + VersionUtilities.getCurrentVersion(
        PACKAGE_VERSION);
    this.validationEngine = new ValidationEngine(definitions, FhirPublication.R4, PACKAGE_VERSION, new TimeTracker());
    //Loading structure definitions from official package and uploading custom definitions if needed from resources
    this.validationEngine.loadPackage(SDOH_CLINICAL_CARE_PACKAGE,SDOH_CLINICAL_CARE_VERSION);
    this.validationEngine.loadIg(ResourceUtils.getURL("classpath:" + CUSTOM_STRUCTURE_DEFINITIONS_LOCATION)
        .getPath(), false);
  }

  public Map<String, Object> convert(JSONObject questionnaireResponse) throws IOException {
    QuestionnaireResponse qs = (QuestionnaireResponse) resourceParser.parseResource(questionnaireResponse.toString());
    Optional<Questionnaire> foundQuestionnaire = questionnaireRepository.findByCanonnicalUri(qs.getQuestionnaire());
    if (!foundQuestionnaire.isPresent()) {
      throw new ResourceNotFoundException(
          String.format("Questionnaire was not found by URL '%s'.", qs.getQuestionnaire()));
    }
    Questionnaire questionnaire = foundQuestionnaire.get();
    String mapUri = ((CanonicalType) questionnaire.getExtensionByUrl(MAP_EXTENSION)
        .getValue()).getValueAsString();
    boolean mapExists = validationEngine.getContext()
        .listTransforms()
        .stream()
        .anyMatch(map -> map.getUrl().equals(mapUri));
    if (!mapExists) {
      loadMapIg(mapUri);
    }
    Bundle result = convertToBundle(questionnaireResponse, mapUri);
    result.addEntry(toEntry(qs));
    return new ObjectMapper().readValue(resourceParser.encodeResourceToString(result), Map.class);
  }

  private Bundle convertToBundle(JSONObject questionnaireResponse, String mapUri) {
    String map;
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      Element element = validationEngine.transform(questionnaireResponse.toString()
          .getBytes(), Manager.FhirFormat.JSON, mapUri);
      new JsonParser(validationEngine.getContext()).compose(element, byteArrayOutputStream,
          org.hl7.fhir.r5.formats.IParser.OutputStyle.PRETTY, null);
      map = new String(byteArrayOutputStream.toByteArray());
    } catch (IOException e) {
      throw new IllegalStateException(String.format("QuestionnaireReponse with id cannot be parsed."), e.getCause());
    } return (Bundle) resourceParser.parseResource(map);
  }

  private void loadMapIg(String mapUri) throws IOException {
    Path mapIg = Files.createTempDirectory("mapIg");
    Optional<StructureMap> foundStructureMap = structureMapRepository.findByUrl(mapUri);
    if (!foundStructureMap.isPresent()) {
      throw new ResourceNotFoundException(String.format("StructureMap was not found by URL '%s'.", mapUri));
    }
    StructureMap structureMap = foundStructureMap.get();
    File jsonMap = new File(mapIg.toString() + "/map.json");
    jsonMap.createNewFile();
    try (FileWriter fileWriter = new FileWriter(jsonMap);) {
      fileWriter.write(resourceParser.encodeResourceToString(structureMap));
      fileWriter.flush();
    }
    validationEngine.loadIg(mapIg.toString(), false);
    Files.delete(jsonMap.toPath());
    Files.delete(mapIg);
  }

  private Bundle.BundleEntryComponent toEntry(Resource resource) {
    Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
    entry.setResource(resource);
    entry.setFullUrl(resource.getIdElement()
        .getValue());
    return entry;
  }

}
