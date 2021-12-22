package org.hl7.gravity.refimpl.sdohexchange.dto.request.patienttask;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NewSocialRiskTaskRequestDto extends NewPatientTaskRequestDto {

  @NotNull(message = "Questionnaire type (questionnaireType) can't be null.")
  private QuestionnaireType questionnaireType;
  @NotNull(message = "Questionnaire format (questionnaireFormat) can't be null.")
  private QuestionnaireFormat questionnaireFormat;
  @NotEmpty(message = "Questionnaire id (questionnaireId) can't be empty.")
  private String questionnaireId;

}
