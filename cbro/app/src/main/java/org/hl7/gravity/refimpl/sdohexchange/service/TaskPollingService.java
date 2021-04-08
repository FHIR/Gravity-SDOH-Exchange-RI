package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.hl7.gravity.refimpl.sdohexchange.fhir.TaskProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class TaskPollingService {

  private final IGenericClient openCbroClient;
  private final TaskProcessor taskProcessor;

  @Scheduled(fixedDelayString = "${scheduling.task-polling-delay-millis}")
  public void processTasks() {
    log.info("Checking for received tasks from EHR...");
    Bundle searchBundle = openCbroClient.search()
        .forResource(Task.class)
        .where(new TokenClientParam(Constants.PARAM_PROFILE).exactly()
            .code(SDOHProfiles.TASK))
        .and(Task.STATUS.exactly()
            .code(Task.TaskStatus.RECEIVED.toCode()))
        .returnBundle(Bundle.class)
        .execute();

    Bundle transactionBundle = new Bundle();
    transactionBundle.setType(Bundle.BundleType.TRANSACTION);

    FhirUtil.getFromBundle(searchBundle, Task.class)
        .stream()
        .map(taskProcessor::process)
        .forEach(bundle -> transactionBundle.getEntry()
            .addAll(bundle.getEntry()));
    if (transactionBundle.getEntry()
        .size() > 0) {
      log.info("One or more task were accepted from EHR. Storing updates to CBRO...");
      openCbroClient.transaction()
          .withBundle(transactionBundle)
          .execute();
    }
    log.info("Task accepting process finished.");
  }

}