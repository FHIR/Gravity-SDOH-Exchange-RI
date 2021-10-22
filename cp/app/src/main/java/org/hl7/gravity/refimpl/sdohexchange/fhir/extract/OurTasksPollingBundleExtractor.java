package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.OurTasksPollingBundleExtractor.OurTasksPollingInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OurTasksPollingBundleExtractor extends BundleExtractor<OurTasksPollingInfo> {

  @Override
  public OurTasksPollingInfo extract(Bundle bundle) {
    Map<? extends Class<? extends Resource>, Map<String, Resource>> taskResources = bundle.getEntry()
        .stream()
        .map(BundleEntryComponent::getResource)
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(Resource::getClass, Collectors.toMap(r -> r.getIdElement()
            .getIdPart(), Function.identity())));
    return new OurTasksPollingInfo(taskResources);
  }

  public class OurTasksPollingInfo {

    //task Id -> task
    private final Map<String, Task> tasks;
    //task Id (original, not own) -> own task
    private final Map<String, Task> ownTasks;

    public OurTasksPollingInfo(Map<? extends Class<? extends Resource>, Map<String, Resource>> taskResources) {
      Map<String, Task> allTasks = collectResources(taskResources, Task.class);
      this.tasks = allTasks.entrySet()
          .stream()
          .filter(e -> !e.getValue()
              .hasBasedOn())
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      this.ownTasks = allTasks.entrySet()
          .stream()
          .filter(e -> e.getValue()
              .hasBasedOn())
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Collection<Task> getTasks() {
      return tasks.values();
    }

    public Collection<Task> getOurTasks() {
      return ownTasks.values();
    }

    public Task getTask(String id) {
      return tasks.get(id);
    }

    public Task getOurTask(Task task) throws OurTaskPollingUpdateException {
      List<Task> tasks = getOurTasks().stream()
          .filter(t -> t.getBasedOn()
              .stream()
              .filter(b -> task.getIdElement()
                  .toUnqualifiedVersionless()
                  .equals(b.getReference()))
              .findAny()
              .isPresent())
          .collect(Collectors.toList());
      if (tasks.size() == 0) {
        String reason = String.format("No own task is found for task with id '%s'", task.getIdElement()
            .getIdPart());
        throw new OurTaskPollingUpdateException(reason);
      } else if (tasks.size() > 1) {
        String reason = String.format("%d own tasks were found for task with id '%s'. Only one is expected.",
            tasks.size(), task.getIdElement()
                .getIdPart());
        throw new OurTaskPollingUpdateException(reason);
      }
      return tasks.get(0);
    }

    private <R extends Resource> Map<String, R> collectResources(
        Map<? extends Class<? extends Resource>, Map<String, Resource>> resources, Class<R> resourceClass) {
      return Optional.ofNullable(resources.get(resourceClass))
          .orElse(Collections.emptyMap())
          .entrySet()
          .stream()
          .collect(Collectors.toMap(Entry::getKey, entry -> resourceClass.cast(entry.getValue())));
    }
  }

  /**
   * Exception that should be thrown when task update process failed. This will usually lead to a Task with a status
   * FAILED and corresponding statusReason.
   */
  public static class OurTaskPollingUpdateException extends Exception {

    public OurTaskPollingUpdateException(String message) {
      super(message);
    }
  }
}