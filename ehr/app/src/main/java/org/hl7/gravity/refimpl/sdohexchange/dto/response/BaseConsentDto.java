package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Basic info about Consent resource.
 *
 * @author Mykhailo Stefantsiv
 */
@Getter
@AllArgsConstructor
public class BaseConsentDto {

  private String id;
  private String name;
}
