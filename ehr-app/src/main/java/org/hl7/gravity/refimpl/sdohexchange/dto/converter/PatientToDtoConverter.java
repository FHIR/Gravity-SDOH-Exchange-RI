package org.hl7.gravity.refimpl.sdohexchange.dto.converter;

import com.google.common.base.Strings;
import org.apache.commons.lang3.ObjectUtils;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.PatientDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.util.FhirUtil;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;

public class PatientToDtoConverter implements Converter<Patient, PatientDto> {

  @Override
  public PatientDto convert(Patient patient) {
    PatientDto patientDto = new PatientDto(patient.getIdElement()
        .getIdPart());
    patientDto.setName(patient.getNameFirstRep()
        .getNameAsSingleString());
    patientDto.setGender(ObjectUtils.defaultIfNull(patient.getGender(), Enumerations.AdministrativeGender.UNKNOWN)
        .getDisplay());

    //Get Date of Birth and Age
    if (patient.getBirthDate() != null) {
      LocalDate dob = FhirUtil.toLocalDate(patient.getBirthDateElement());
      patientDto.setDob(dob);
      patientDto.setAge(Period.between(dob, LocalDate.now(ZoneOffset.UTC))
          .getYears());
    }
    //Get Address full String. No need to compose it on UI side.
    Address address = patient.getAddress()
        .stream()
        .filter(a -> Address.AddressUse.HOME.equals(a.getUse()))
        .findFirst()
        .orElse(null);
    if (address != null) {
      StringBuilder sb = new StringBuilder().append(address.hasLine() ? address.getLine()
          .get(0) + "\n" : "");
      sb.append(Strings.isNullOrEmpty(address.getCity()) ? "" : address.getCity() + ", ");
      sb.append(Strings.isNullOrEmpty(address.getState()) ? "" : address.getState() + " ");
      sb.append(Strings.isNullOrEmpty(address.getPostalCode()) ? "" : address.getPostalCode());
      patientDto.setAddress(sb.toString());
    }

    return patientDto;
  }
}