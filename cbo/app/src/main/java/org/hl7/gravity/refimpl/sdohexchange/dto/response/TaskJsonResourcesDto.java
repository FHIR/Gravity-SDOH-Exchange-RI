package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TaskJsonResourcesDto {

  private String task;
  private String serviceRequest;
  private String patient;
  private String requester;
  private String consent;
  private List<String> goals;
  private List<String> conditions;
  private List<String> procedures;
}
