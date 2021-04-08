package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.gravity.refimpl.sdohexchange.codesystems.OrganizationTypeCode;
import org.hl7.gravity.refimpl.sdohexchange.dto.Validated;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class OrganizationDto implements Validated {

  private final String organizationId;
  private String name;
  private OrganizationTypeCode type;

  @Setter(AccessLevel.NONE)
  private List<String> errors = new ArrayList<>();
}
