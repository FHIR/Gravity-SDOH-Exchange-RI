package org.hl7.gravity.refimpl.sdohexchange.dao.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.gravity.refimpl.sdohexchange.dao.FhirRepository;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * @author Mykhailo Stefantsiv
 */
@Component
public class QuestionnaireRepository extends FhirRepository<Questionnaire> {

  //TODO: use secured ehrClient
  public QuestionnaireRepository(IGenericClient openEhrClient) {
    super(openEhrClient);
  }

  public Optional<Questionnaire> findByCanonnicalUri(String uri) {
    Assert.notNull(uri, "CannonnicalUri should be specified");
    Bundle bundle = getClient().search()
        .forResource(Questionnaire.class)
        .where(Questionnaire.URL.matches()
            .value(uri))
        .returnBundle(Bundle.class)
        .execute();
    return Optional.ofNullable(FhirUtil.getFromBundle(bundle, Questionnaire.class)
        .stream()
        .findFirst()
        .orElse(null));
  }

  @Override
  public Class<Questionnaire> getResourceType() {
    return Questionnaire.class;
  }
}
