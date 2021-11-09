package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.OurTaskInfoBundleExtractor.OurTaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor.TaskInfoHolder;

import java.util.List;
import java.util.stream.Collectors;

public class OurTaskInfoBundleExtractor extends BundleExtractor<List<OurTaskInfoHolder>> {

  private TaskInfoBundleExtractor taskInfoBundleExtractor = new TaskInfoBundleExtractor();

  @Override
  public List<OurTaskInfoHolder> extract(Bundle bundle) {
    List<TaskInfoBundleExtractor.TaskInfoHolder> taskInfoHolders = taskInfoBundleExtractor.extract(bundle);

    return taskInfoHolders.stream()
        .filter(t -> t.getTask()
            .getIntent() == Task.TaskIntent.FILLERORDER)
        .map(taskInfoHolder -> {
          Task baseTask = (Task) taskInfoHolder.getTask()
              .getBasedOn()
              .get(0)
              .getResource();

          return new OurTaskInfoHolder(taskInfoHolder, baseTask);
        })
        .collect(Collectors.toList());
  }

  @Getter
  public static class OurTaskInfoHolder extends TaskInfoHolder {

    private final Task baseTask;

    public OurTaskInfoHolder(TaskInfoHolder taskInfoHolder, Task baseTask) {
      super(taskInfoHolder.getTask(), taskInfoHolder.getServiceRequest());
      this.baseTask = baseTask;
    }
  }
}
