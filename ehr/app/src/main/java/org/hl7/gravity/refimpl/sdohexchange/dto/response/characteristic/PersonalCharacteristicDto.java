package org.hl7.gravity.refimpl.sdohexchange.dto.response.characteristic;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicMethod;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.CodingDto;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.ReferenceDto;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalCharacteristicDto {

  private String id;
  private CharacteristicCode type;
  private CharacteristicMethod method;
  private String methodDetail;
  private CodingDto value;
  private String valueDetail;
  // Only for race
  private List<CodingDto> values;
  // Only for race and ethnicity
  private List<CodingDto> detailedValues;
  // Only for race and ethnicity
  private String description;
  private ReferenceDto performer;
  //Only for the reported sex and gender
  private Boolean hasAttachment;
}
