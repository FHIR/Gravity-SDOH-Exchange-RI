package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PatientDto extends PersonDto {

  private Integer age;
  private LocalDate dob;
  private String gender;
  private String language;
  private String address;
  private String employmentStatus;
  private String race;
  private String ethnicity;
  private String education;
  private String maritalStatus;

  private List<String> insurances = new ArrayList<>();
  private List<PhoneDto> phones = new ArrayList<>();
  private List<EmailDto> emails = new ArrayList<>();
}