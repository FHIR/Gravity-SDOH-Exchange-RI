package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
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
  private List<AssessmentItem> item;
  private List<AssessmentDto> previous;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();

  @Getter
  @Setter
  public static class AssessmentItem {

    private String text;
    private List<AssessmentAnswer> answer;
    private List<AssessmentItem> item;
  }

  @Getter
  @Setter
  public static class AssessmentAnswer {

    @JsonValue
    private TypeDto value;
  }
}
