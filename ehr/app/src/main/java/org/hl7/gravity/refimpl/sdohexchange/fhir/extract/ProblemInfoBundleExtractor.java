package org.hl7.gravity.refimpl.sdohexchange.fhir.extract;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO refactor this class. extending ConditionInfoBundleExtractor and aggregating it looks smelly in this specific
// case.
public class ProblemInfoBundleExtractor extends ConditionInfoBundleExtractor {

  private ConditionInfoBundleExtractor conditionInfoBundleExtractor = new ConditionInfoBundleExtractor();

  @Override
  public List<ProblemInfoHolder> extract(Bundle bundle) {
    List<? extends ConditionInfoHolder> conditionInfoHolders = conditionInfoBundleExtractor.extract(bundle);
    Map<String, ProblemInfoHolder> idToHolderMap = new HashMap<>();
    for (ConditionInfoHolder h : conditionInfoHolders) {
      idToHolderMap.put(h.getCondition()
          .getIdElement()
          .getIdPart(), new ProblemInfoHolder(h));
    }

    for (Task task : FhirUtil.getFromBundle(bundle, Task.class)) {
      if (!task.hasFocus() || !(task.getFocus()
          .getResource() instanceof ServiceRequest)) {
        continue;
      }
      ServiceRequest sr = (ServiceRequest) task.getFocus()
          .getResource();
      sr.getReasonReference()
          .stream()
          .filter(ref -> Condition.class.getSimpleName()
              .equals(ref.getReferenceElement()
                  .getResourceType()) && idToHolderMap.containsKey(ref.getReferenceElement()
              .getIdPart()))
          .forEach(ref -> idToHolderMap.get(ref.getReferenceElement()
              .getIdPart())
              .getTasks()
              .add(new TaskInfoHolder(task.getIdElement()
                  .getIdPart(), task.getDescription(), task.getStatus())));
    }

    for (Goal goal : FhirUtil.getFromBundle(bundle, Goal.class)) {
      goal.getAddresses()
          .stream()
          .filter(ref -> Condition.class.getSimpleName()
              .equals(ref.getReferenceElement()
                  .getResourceType()) && idToHolderMap.containsKey(ref.getReferenceElement()
              .getIdPart()))
          .forEach(ref -> {
            idToHolderMap.get(ref.getReferenceElement()
                .getIdPart())
                .getGoals()
                .add(new GoalInfoHolder(goal.getIdElement()
                    .getIdPart(), goal.getDescription()
                    .getText(), goal.getLifecycleStatus()));
          });
    }

    return new ArrayList<>(idToHolderMap.values());
  }

  @Getter
  public static class ProblemInfoHolder extends ConditionInfoHolder {

    private List<TaskInfoHolder> tasks = new ArrayList<>();
    private List<GoalInfoHolder> goals = new ArrayList<>();

    public ProblemInfoHolder(ConditionInfoHolder conditionInfoHolder) {
      super(conditionInfoHolder.getCondition(), conditionInfoHolder.getQuestionnaireResponse(),
          conditionInfoHolder.getObservations());
    }
  }

  @Getter
  @RequiredArgsConstructor
  public static class TaskInfoHolder {

    private final String id;
    private final String name;
    private final Task.TaskStatus status;
  }

  @Getter
  @RequiredArgsConstructor
  public static class GoalInfoHolder {

    private final String id;
    private final String name;
    private final Goal.GoalLifecycleStatus status;
  }
}