package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NewTaskRequestDto {

  @NotEmpty(message = "Task name can't be empty.")
  private String name;
  @NotEmpty(message = "SDOH category can't be empty.")
  private String category;
  @NotEmpty(message = "SDOH code can't be empty.")
  private String code;
  private String comment;
  @NotNull
  private Priority priority;
  @NotNull
  private OccurrenceRequestDto occurrence;
  private List<String> conditionIds = new ArrayList<>();
  private List<String> goalIds = new ArrayList<>();
  @NotEmpty(message = "Performer Organization id can't be empty.")
  private String performerId;
  @NotNull
  private String consent;
}
