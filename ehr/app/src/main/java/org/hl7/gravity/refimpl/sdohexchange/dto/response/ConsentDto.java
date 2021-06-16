package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Contains all information about consent resource.
 *
 * @author Mykhailo Stefantsiv
 */
@Getter
public class ConsentDto extends BaseConsentDto {

  private String scope;
  private String status;
  private String organization;
  private String category;
  private LocalDateTime consentDate;

  @Builder
  public ConsentDto(String id, String name, String scope, String status, String organization,
      String category, LocalDateTime consentDate) {
    super(id, name);
    this.scope = scope;
    this.status = status;
    this.organization = organization;
    this.category = category;
    this.consentDate = consentDate;
  }
}
