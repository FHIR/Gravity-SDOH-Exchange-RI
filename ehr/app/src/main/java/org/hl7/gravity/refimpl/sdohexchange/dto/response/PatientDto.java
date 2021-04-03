package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientDto extends PersonDto {

  private String gender;
  private Integer age;
  private LocalDate dob;
  private String address;

  public PatientDto(String id) {
    super(id);
  }
}