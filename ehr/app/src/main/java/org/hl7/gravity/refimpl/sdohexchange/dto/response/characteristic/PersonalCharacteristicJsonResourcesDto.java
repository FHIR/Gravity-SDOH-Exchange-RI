package org.hl7.gravity.refimpl.sdohexchange.dto.response.characteristic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class PersonalCharacteristicJsonResourcesDto {

  private String personalCharacteristic;
  private String patient;
  private String performer;
  private String documentReference;
}
