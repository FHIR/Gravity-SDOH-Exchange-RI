package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Mykhailo Stefantsiv
 */
@Builder
@Getter
public class AttachmentDto {

  private String title;
  private byte[] content;
  private String contentType;
}
