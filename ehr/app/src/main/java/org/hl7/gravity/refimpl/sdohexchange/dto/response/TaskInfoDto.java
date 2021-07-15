package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.dto.Validated;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class TaskInfoDto implements Validated {

  private final String id;
  private final String name;
  private final Task.TaskStatus status;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();
}