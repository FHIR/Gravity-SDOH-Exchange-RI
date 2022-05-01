package org.hl7.gravity.refimpl.sdohexchange.service;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.gravity.refimpl.sdohexchange.dao.impl.ObservationRepository;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.characteristic.PersonalCharacteristicJsonResourcesDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.ResourceParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ResourceService {

  private final ObservationRepository observationRepository;
  private final ResourceParser resourceParser;

  public PersonalCharacteristicJsonResourcesDto getCharacteristicResources(String id) {
    Observation characteristic = observationRepository.getCharacteristicWithResources(id);
    Patient patient = (Patient) characteristic.getSubject()
        .getResource();
    Practitioner performer = (Practitioner) characteristic.getPerformerFirstRep()
        .getResource();
    return new PersonalCharacteristicJsonResourcesDto().setPersonalCharacteristic(resourceParser.parse(characteristic))
        .setPatient(resourceParser.parse(patient))
        .setPerformer(resourceParser.parse(performer));
  }
}
