package org.hl7.gravity.refimpl.sdohexchange.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ServerDto {

  private Integer id;
  private String serverName;
  private String fhirServerUrl;
  private String authServerUrl;
  private String clientId;
  private String clientSecret;
  private OffsetDateTime lastSyncDate = OffsetDateTime.now();
}
