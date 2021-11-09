package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import lombok.Getter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.CpTaskInfoBundleExtractor.CpTaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.TaskInfoBundleExtractor.TaskInfoHolder;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CpTaskInfoBundleExtractor extends BundleExtractor<List<CpTaskInfoHolder>> {

  private TaskInfoBundleExtractor taskInfoBundleExtractor = new TaskInfoBundleExtractor();

  @Override
  public List<CpTaskInfoHolder> extract(Bundle bundle) {
    List<TaskInfoHolder> taskInfoHolders = taskInfoBundleExtractor.extract(bundle);
    Map<String, Task> ourTaskMap = FhirUtil.getFromBundle(bundle, Task.class)
        .stream()
        .filter(t -> t.getIntent() == Task.TaskIntent.FILLERORDER)
        .collect(Collectors.toMap(task -> task.getBasedOn()
            .get(0)
            .getResource()
            .getIdElement()
            .getIdPart(), Function.identity()));

    return taskInfoHolders.stream()
        .filter(t -> t.getTask()
            .getIntent() == Task.TaskIntent.ORDER)
        .map(taskInfoHolder -> {
          Task ourTask = ourTaskMap.get(taskInfoHolder.getTask()
              .getIdElement()
              .getIdPart());
          Organization performer = null;
          if (!Objects.isNull(ourTaskMap.get(taskInfoHolder.getTask()
              .getIdElement()
              .getIdPart()))) {
            performer = (Organization) ourTaskMap.get(taskInfoHolder.getTask()
                    .getIdElement()
                    .getIdPart())
                .getOwner()
                .getResource();
          }
          return new CpTaskInfoHolder(taskInfoHolder, ourTask, performer);
        })
        .collect(Collectors.toList());
  }

  @Getter
  public static class CpTaskInfoHolder extends TaskInfoHolder {

    private final Task ourTask;
    private final Organization performer;

    public CpTaskInfoHolder(TaskInfoHolder taskInfoHolder, Task ourTask, Organization performer) {
      super(taskInfoHolder.getTask(), taskInfoHolder.getServiceRequest());
      this.ourTask = ourTask;
      this.performer = performer;
    }
  }
}
