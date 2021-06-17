package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import ca.uhn.fhir.rest.api.Constants;
import lombok.AllArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCoreProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

/**
 * Creates bundle to retrieve all data which is necessary for Consent resource creation.
 *
 * @author Mykhailo Stefantsiv
 */
@AllArgsConstructor
public class ConsentPrepareBundleFactory extends PrepareBundleFactory {

  private String practitioner;

  @Override
  public Bundle createPrepareBundle() {
    Bundle prepareBundle = new Bundle();
    prepareBundle.setType(Bundle.BundleType.TRANSACTION);
    prepareBundle.addEntry(getPractitionerRoleEntry());
    return prepareBundle;
  }

  protected Bundle.BundleEntryComponent getPractitionerRoleEntry() {
    Assert.notNull(practitioner, "Practitioner can't be null.");
    return FhirUtil.createGetEntry(addParams(PractitionerRole.class.getSimpleName(),
        combineParams(eq(PractitionerRole.SP_PRACTITIONER, practitioner),
            eq(Constants.PARAM_PROFILE, UsCoreProfiles.PRACTITIONER_ROLE),
            eq(resourceField(PractitionerRole.SP_ORGANIZATION, Constants.PARAM_PROFILE),
                UsCoreProfiles.ORGANIZATION))));
  }
}
