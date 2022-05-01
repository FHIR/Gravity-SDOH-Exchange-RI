package org.hl7.gravity.refimpl.sdohexchange.dto.response.characteristic;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonalCharacteristicJsonResourcesDto {

  private String personalCharacteristic;
  private String patient;
  private String performer;
}
