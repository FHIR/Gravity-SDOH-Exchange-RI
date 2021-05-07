package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Contains an ID and @{@link org.hl7.fhir.r4.model.Consent} display, from Scope CodeableConcept.
 *
 * @author Mykhailo Stefantsiv
 */
@AllArgsConstructor
@Getter
public class ConsentResponseDto {

  private String id;
  private String display;
}
