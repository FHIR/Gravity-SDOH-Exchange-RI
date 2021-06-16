package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Contains all information about consent resource.
 *
 * @author Mykhailo Stefantsiv
 */
@Getter
public class ConsentDto extends BaseConsentDto {

  private String scope;
  private String status;
  private byte[] attachment;
  private String organization;

  @Builder
  public ConsentDto(String id, String name, String scope, String status, byte[] attachment, String organization) {
    super(id, name);
    this.scope = scope;
    this.status = status;
    this.attachment = attachment;
    this.organization = organization;
  }
}
