package org.hl7.gravity.refimpl.sdohexchange.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Server {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  @Setter(AccessLevel.NONE)
  private Integer id;

  @Column(unique = true, nullable = false, length = 64)
  private String serverName;

  @Column(nullable = false)
  private String fhirServerUrl;

  @Column(nullable = false)
  private String authServerUrl;

  @Column(nullable = false)
  private String clientId;

  @Column(nullable = false)
  private String clientSecret;
}
