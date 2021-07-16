package org.hl7.gravity.refimpl.sdohexchange.fhir.query;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import org.hl7.fhir.instance.model.api.IBaseBundle;

/**
 * Factory of queries to retrieve patient specific resources.
 */
public interface FHIRQueryFactory {

  IQuery<IBaseBundle> query(IGenericClient client, String patientId);
}
