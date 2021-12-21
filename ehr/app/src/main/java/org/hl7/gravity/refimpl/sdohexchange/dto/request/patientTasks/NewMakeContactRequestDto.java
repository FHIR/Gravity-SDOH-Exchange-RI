package org.hl7.gravity.refimpl.sdohexchange.dto.request.patientTasks;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class NewMakeContactRequestDto extends NewPatientTaskRequestDto {

  @NotEmpty(message = "Referral Task id (referralTaskId) can't be empty.")
  private String referralTaskId;
  @NotEmpty(message = "Healthcare Service id (healthcareServiceId) can't be empty.")
  private String healthcareServiceId;
}
