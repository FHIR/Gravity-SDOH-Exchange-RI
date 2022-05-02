package org.hl7.gravity.refimpl.sdohexchange.service;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.ObservationRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.characteristic.PersonalCharacteristicJsonResourcesDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.ResourceParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ResourceService {

  private final ObservationRepository observationRepository;
  private final ResourceParser resourceParser;

  public PersonalCharacteristicJsonResourcesDto getCharacteristicResources(String id) {
    Observation characteristic = observationRepository.getCharacteristicWithResources(id);

    String patientString = Optional.ofNullable(characteristic.getSubject()
            .getResource())
        .filter(Patient.class::isInstance)
        .map(Patient.class::cast)
        .map(resourceParser::parse)
        .orElse(null);

    //Expected either Patient or Practitioner
    String performerString = Optional.ofNullable(characteristic.getPerformerFirstRep()
            .getResource())
        .map(Resource.class::cast)
        .map(resourceParser::parse)
        .orElse(null);

    String drString = Optional.ofNullable(characteristic.getDerivedFromFirstRep()
            .getResource())
        .filter(DocumentReference.class::isInstance)
        .map(DocumentReference.class::cast)
        .map(resourceParser::parse)
        .orElse(null);
    return new PersonalCharacteristicJsonResourcesDto().setPersonalCharacteristic(resourceParser.parse(characteristic))
        .setPatient(patientString)
        .setPerformer(performerString)
        .setDocumentReference(drString);
  }
}
