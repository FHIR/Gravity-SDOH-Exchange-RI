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
  private String requestName;
  @NotNull
  private SDOHDomainCode category;
  private Priority priority;
  //TODO: Add occurrence
  @NotNull
  private RequestCode request;
  private String details;
  @NotNull
  private String performerId;
  @NotNull
  private Boolean consent;
  private List<String> conditionIds = new ArrayList<>();
  private List<String> goalIds = new ArrayList<>();
}
