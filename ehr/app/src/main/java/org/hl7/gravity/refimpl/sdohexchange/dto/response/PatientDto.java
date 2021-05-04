package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientDto extends PersonDto {

  private Integer age;
  private LocalDate dob;
  private String gender;
  private String language;
  private String address;
  private String phone;
  private String email;
  //TODO: Set from Observation?
  private String employmentStatus;
  private String race;
  private String ethnicity;
  //TODO: Set from Observation?
  private String education;
  private String maritalStatus;
  //TODO: Set from Coverage
  private String insurance;

  public PatientDto(String id) {
    super(id);
  }
}