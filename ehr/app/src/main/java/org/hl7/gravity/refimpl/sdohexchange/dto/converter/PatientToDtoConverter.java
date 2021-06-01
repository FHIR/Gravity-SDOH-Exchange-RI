package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import com.google.common.base.Strings;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.EmailDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.PatientDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.PhoneDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCorePatientExtensions;
import org.hl7.gravity.refimpl.sdohexchange.info.PatientInfo;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

public class PatientToDtoConverter implements Converter<PatientInfo, PatientDto> {

  @Override
  public PatientDto convert(PatientInfo patientInfo) {
    Patient patient = patientInfo.getPatient();
    PatientDto patientDto = new PatientDto();
    patientDto.setId(patient.getIdElement()
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
    //Get phone numbers
    patientDto.getPhones()
        .addAll(telecom.stream()
            .filter(t -> ContactPoint.ContactPointSystem.PHONE.equals(t.getSystem()))
            .map(cp -> {
              String display = cp.getUse() == null
                  ? null
                  : cp.getUse()
                      .getDisplay();
              return new PhoneDto(display, cp.getValue());
            })
            .collect(Collectors.toList()));
    //Get email addreses
    patientDto.getEmails()
        .addAll(telecom.stream()
            .filter(t -> ContactPoint.ContactPointSystem.EMAIL.equals(t.getSystem()))
            .map(cp -> {
              String display = cp.getUse() == null
                  ? null
                  : cp.getUse()
                      .getDisplay();
              return new EmailDto(display, cp.getValue());
            })
            .collect(Collectors.toList()));
    if (patientInfo.getEmployment() != null) {
      String employmentStatus = patientInfo.getEmployment()
          .getValueCodeableConcept()
          .getCodingFirstRep()
          .getDisplay();
      patientDto.setEmploymentStatus(employmentStatus);
    }
    if (patientInfo.getEducation() != null) {
      String education = patientInfo.getEducation()
          .getValueCodeableConcept()
          .getCodingFirstRep()
          .getDisplay();
      patientDto.setEducation(education);
    }
    //Get race
    Extension race = patient.getExtensionByUrl(UsCorePatientExtensions.RACE);
    patientDto.setRace(convertExtension(race));
    //Get ethnicity
    Extension ethnicity = patient.getExtensionByUrl(UsCorePatientExtensions.ETHNICITY);
    patientDto.setEthnicity(convertExtension(ethnicity));
    //Get marital status
    patientDto.setMaritalStatus(Optional.ofNullable(patient.getMaritalStatus()
        .getCodingFirstRep()
        .getDisplay())
        .orElse(null));
    patientDto.getInsurances()
        .addAll(convertPayors(patientInfo.getPayors()));
    return patientDto;
  }

  private List<String> convertPayors(List<IBaseResource> payors) {
    List<String> payorsNames = new ArrayList<>();
    for (IBaseResource resource : payors) {
      if (resource instanceof Patient) {
        payorsNames.add(((Patient) resource).getNameFirstRep()
            .getNameAsSingleString());
      } else if (resource instanceof Organization) {
        payorsNames.add(((Organization) resource).getName());
      } else if (resource instanceof RelatedPerson) {
        payorsNames.add(((RelatedPerson) resource).getNameFirstRep()
            .getNameAsSingleString());
      } else {
        throw new IllegalStateException("Not valid payor resource type : " + resource.getClass());
      }
    }
    return payorsNames;
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