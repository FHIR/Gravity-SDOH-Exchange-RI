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
public class AssessmentDto implements Validated {

  private String id;
  private String name;
  private String questionnaireUrl;
  private LocalDateTime date;
  private List<TypeDto> healthConcerns;
  private List<AssessmentResponse> assessmentResponse;
  private List<AssessmentDto> previous;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();

  @Getter
  @Setter
  public static class AssessmentResponse {

    private TypeDto question;
    private TypeDto answer;
  }
}