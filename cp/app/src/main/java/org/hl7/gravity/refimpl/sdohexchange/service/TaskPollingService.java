package org.hl7.gravity.refimpl.sdohexchange.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.fhir.TaskProcessor;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskPollingService {

  private final IGenericClient openCpClient;
  private final TaskProcessor taskProcessor;

  @Scheduled(fixedDelayString = "${scheduling.task-polling-delay-millis}")
  public void processTasks() {
    log.info("Checking for requested tasks from EHR...");
    Bundle searchBundle = openCpClient.search()
        .forResource(Task.class)
        //TODO: Add filter back after bug fix with sending task with profile
        //        .where(new TokenClientParam(Constants.PARAM_PROFILE).exactly()
        //            .code(SDOHProfiles.TASK))
        .and(Task.STATUS.exactly()
            .code(Task.TaskStatus.REQUESTED.toCode()))
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
      log.info("One or more task were received from EHR. Storing updates...");
      openCpClient.transaction()
          .withBundle(transactionBundle)
          .execute();
    }
    log.info("Task receiving process finished.");
  }
}