package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hl7.gravity.refimpl.sdohexchange.dto.Validated;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthConcernDto implements Validated {

  private String id;
  private String name;
  private CodingDto category;
  private TypeDto basedOn;
  private TypeDto authoredBy;
  private LocalDateTime date;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();
}