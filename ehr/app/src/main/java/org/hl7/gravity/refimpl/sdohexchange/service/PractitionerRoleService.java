package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCoreProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PractitionerRoleService {

  private final IGenericClient ehrClient;

  /**
   * Search for role with US Core profile which references US Core Organization.
   *
   * @param practitionerId the practitioner owner id
   * @return found role, otherwise null
   */
  public PractitionerRole getRole(String practitionerId) {
    Bundle bundle = ehrClient.search()
        .forResource(PractitionerRole.class)
        .where(PractitionerRole.PRACTITIONER.hasId(practitionerId))
        .and(new TokenClientParam(Constants.PARAM_PROFILE).exactly()
            .code(UsCoreProfiles.PRACTITIONER_ROLE))
        .and(new TokenClientParam(PractitionerRole.SP_ORGANIZATION + "." + Constants.PARAM_PROFILE).exactly()
            .code(UsCoreProfiles.ORGANIZATION))
        .returnBundle(Bundle.class)
        .execute();

    List<PractitionerRole> roles = FhirUtil.getFromBundle(bundle, PractitionerRole.class);
    if (roles.isEmpty()) {
      throw new IllegalStateException(
          "No Practitioner role with US Core profile which references to US Core Organization have been found.");
    } else if (roles.size() > 1) {
      throw new IllegalStateException(
          "More than one Practitioner role with US Core profile which references to US Core Organization have been "
              + "found.");
    }
    return roles.stream()
        .findFirst()
        .orElse(null);
  }
}
