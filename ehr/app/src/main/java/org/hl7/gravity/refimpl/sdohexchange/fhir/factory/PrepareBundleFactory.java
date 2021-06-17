package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import org.hl7.fhir.r4.model.Bundle;

/**
 * @author Mykhailo Stefantsiv
 */
public abstract class PrepareBundleFactory {

  public abstract Bundle createPrepareBundle();

  protected String path(String resource, String id) {
    return resource + "/" + id;
  }

  protected String addParams(String url, String params) {
    return url + "?" + params;
  }

  protected String hasSystemWithAnyCode(String system) {
    return system + "|";
  }

  protected String hasSystemAndCode(String system, String code) {
    return hasSystemWithAnyCode(system) + code;
  }

  protected String resourceField(String resource, String field) {
    return resource + "." + field;
  }

  protected String eq(String param, String value) {
    return param + "=" + value;
  }

  protected String include(String resource, String field) {
    return resource + ":" + field;
  }

  protected String combineParams(String... params) {
    return String.join("&", params);
  }
}
