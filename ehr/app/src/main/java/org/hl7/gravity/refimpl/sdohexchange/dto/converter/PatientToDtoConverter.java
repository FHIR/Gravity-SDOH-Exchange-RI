package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import com.google.common.base.Strings;
import org.apache.commons.lang3.ObjectUtils;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.PatientDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCorePatientExtensions;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class PatientToDtoConverter implements Converter<Patient, PatientDto> {


  @Override
  public PatientDto convert(Patient patient) {
    PatientDto patientDto = new PatientDto(patient.getIdElement()
        .getIdPart());
    patientDto.setName(patient.getNameFirstRep()
        .getNameAsSingleString());
    //Get Date of Birth and Age
    if (patient.hasBirthDate()) {
      LocalDate dob = FhirUtil.toLocalDate(patient.getBirthDateElement());
      patientDto.setDob(dob);
      patientDto.setAge(Period.between(dob, LocalDate.now(ZoneOffset.UTC))
          .getYears());
    }
    //Get gender
    patientDto.setGender(ObjectUtils.defaultIfNull(patient.getGender(), Enumerations.AdministrativeGender.UNKNOWN)
        .getDisplay());
    //Get communication language
    patientDto.setLanguage(patient.getCommunication()
        .stream()
        .filter(Patient.PatientCommunicationComponent::getPreferred)
        .map(c -> c.getLanguage()
            .getCodingFirstRep()
            .getDisplay())
        .findFirst()
        .orElse(null));
    //Get Address full String. No need to compose it on UI side.
    patientDto.setAddress(patient.getAddress()
        .stream()
        .filter(a -> Address.AddressUse.HOME.equals(a.getUse()))
        .map(this::convertAddress)
        .findFirst()
        .orElse(null));

    List<ContactPoint> telecom = patient.getTelecom();
    //TODO: Reimplement after clarification
    //Get phone number
    patientDto.setPhone(telecom.stream()
        .filter(t -> ContactPoint.ContactPointSystem.PHONE.equals(t.getSystem()))
        .map(ContactPoint::getValue)
        .findFirst()
        .orElse(null));
    //Get email address
    patientDto.setEmail(telecom.stream()
        .filter(t -> ContactPoint.ContactPointSystem.EMAIL.equals(t.getSystem()))
        .map(ContactPoint::getValue)
        .findFirst()
        .orElse(null));
    //TODO: Set employment status
    //Get race
    Extension race = patient.getExtensionByUrl(UsCorePatientExtensions.RACE);
    patientDto.setRace(convertExtension(race));
    //Get ethnicity
    Extension ethnicity = patient.getExtensionByUrl(UsCorePatientExtensions.ETHNICITY);
    patientDto.setEthnicity(convertExtension(ethnicity));
    //TODO: set education level
    //Get marital status
    patientDto.setMaritalStatus(Optional.ofNullable(patient.getMaritalStatus()
        .getCodingFirstRep()
        .getDisplay())
        .orElse(null));
    //TODO: Set insurance
    return patientDto;
  }

  private String convertAddress(Address address) {
    StringBuilder addressBuilder = new StringBuilder().append(address.hasLine() ? address.getLine()
        .get(0) + "\n" : "");
    addressBuilder.append(Strings.isNullOrEmpty(address.getCity()) ? "" : address.getCity() + ", ");
    addressBuilder.append(Strings.isNullOrEmpty(address.getState()) ? "" : address.getState() + " ");
    addressBuilder.append(Strings.isNullOrEmpty(address.getPostalCode()) ? "" : address.getPostalCode());
    return addressBuilder.toString();
  }

  private String convertExtension(Extension extension) {
    if (extension == null) {
      return null;
    }
    StringType extensionValue = (StringType) extension.getExtensionByUrl("text")
        .getValue();
    return extensionValue.getValue();
  }
}