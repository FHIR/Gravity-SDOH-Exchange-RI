package org.hl7.gravity.refimpl.sdohexchange.dto.request.patientTasks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.OccurrenceRequestDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.request.Priority;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = NewMakeContactTaskRequestDto.class, name = "MAKE_CONTACT"),
    @JsonSubTypes.Type(value = NewSocialRiskTaskRequestDto.class, name = "COMPLETE_SR_QUESTIONNAIRE")})
public class NewPatientTaskRequestDto {

  @NotEmpty(message = "Task name can't be empty.")
  private String name;
  @NotNull(message = "Task type can't be null.")
  private PatientTaskType type;
  @NotEmpty(message = "Task code can't be empty.")
  private String code;

  private String comment;
  @NotNull(message = "Task priority can't be null.")
  private Priority priority;
  @NotNull(message = "Task occurrence can't be null.")
  private OccurrenceRequestDto occurrence;
}
