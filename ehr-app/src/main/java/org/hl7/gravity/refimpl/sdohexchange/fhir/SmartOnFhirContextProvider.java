package org.hl7.gravity.refimpl.sdohexchange.fhir;

import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SmartOnFhirContextProvider {

  private final ApplicationContext applicationContext;

  public SmartOnFhirContext context() {
    // Get new context bean on every call, since it is bound to a current session. If you miss this - the same
    // SmartContext will be returned for every user using an app.
    return applicationContext.getBean(SmartOnFhirContext.class);
  }

}