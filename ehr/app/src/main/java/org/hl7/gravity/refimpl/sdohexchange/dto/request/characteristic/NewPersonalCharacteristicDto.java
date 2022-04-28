package org.hl7.gravity.refimpl.sdohexchange.dto.request.characteristic;

import lombok.Getter;
import lombok.Setter;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicMethod;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NewPersonalCharacteristicDto {

  @NotNull
  private CharacteristicCode type;
  @NotNull
  private CharacteristicMethod method;
  // Required if method is derived or other.
  private String methodDetail;

  // Either value or values is expected depending on characteristic type.
  private String value;
  // Required for "Other" flavored values.
  private String valueDetail;
  // Used only for the race
  private String[] values;
  // Can be set for race and ethnicity.
  private String[] detailedValues;
  // Can be set for race and ethnicity.
  private String description;
}
