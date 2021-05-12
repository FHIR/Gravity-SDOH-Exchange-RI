package org.hl7.gravity.refimpl.sdohexchange.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.RequestCode;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.SDOHDomainCode;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NewTaskRequestDto {

  @NotNull
  private String name;
  @NotNull
  private String category;
  @NotNull
  private String code;
  private String comment;
  @NotNull
  private Priority priority;
  @NotNull
  private OccurrenceRequestDto occurrence;
  private List<String> conditionIds = new ArrayList<>();
  private List<String> goalIds = new ArrayList<>();
  @NotNull
  private String performerId;
  @NotNull
  private Boolean consent;
}
