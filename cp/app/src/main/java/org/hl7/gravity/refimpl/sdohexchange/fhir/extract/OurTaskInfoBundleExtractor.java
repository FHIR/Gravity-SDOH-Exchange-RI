package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.OurTaskInfoBundleExtractor.OurTaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor.TaskInfoHolder;

import java.util.List;
import java.util.stream.Collectors;

public class OurTaskInfoBundleExtractor extends BundleExtractor<List<OurTaskInfoHolder>> {

  private final TaskInfoBundleExtractor taskInfoBundleExtractor = new TaskInfoBundleExtractor();

  @Override
  public List<OurTaskInfoHolder> extract(Bundle bundle) {
    List<TaskInfoHolder> taskInfoHolders = taskInfoBundleExtractor.extract(bundle);

    return taskInfoHolders.stream()
        .filter(t -> t.getTask()
            .getIntent() == Task.TaskIntent.FILLERORDER)
        .map(taskInfoHolder -> {
          Task ourTask = taskInfoHolder.getTask();
          if (ourTask.getBasedOn()
              .isEmpty() || !(ourTask.getBasedOn()
              .get(0)
              .getResource() instanceof Task)) {
            String reason = String.format("Our task resource with id '%s' does not contain basedOn of type Task.",
                ourTask.getIdElement()
                    .getIdPart());
            throw new OurTaskInfoBundleExtractorException(reason);
          }
          Task baseTask = (Task) ourTask.getBasedOn()
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

  public static class OurTaskInfoBundleExtractorException extends RuntimeException {

    public OurTaskInfoBundleExtractorException(String message) {
      super(message);
    }
  }
}
