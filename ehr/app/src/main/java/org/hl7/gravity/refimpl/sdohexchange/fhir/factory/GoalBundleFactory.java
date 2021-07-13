package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import com.google.common.base.Strings;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.codesystems.GoalAchievement;
import org.hl7.gravity.refimpl.sdohexchange.dto.response.UserDto;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

@Setter
public class GoalBundleFactory {

  private String name;
  private Coding category;
  private Coding snomedCode;
  private GoalAchievement achievementStatus;
  private Patient patient;
  private Practitioner practitioner;
  private UserDto user;

  private DateType startDate;
  private List<Condition> problems;
  private String comment;

  public Bundle createBundle() {
    Assert.notNull(name, "Name cannot be null.");
    Assert.notNull(category, "SDOH DomainCode cannot be null.");
    Assert.notNull(snomedCode, "SNOMED-CT code cannot be null.");
    Assert.notNull(achievementStatus, "Achievement status cannot be null.");
    Assert.notNull(patient, "Patient cannot be null.");
    Assert.notNull(practitioner, "Practitioner cannot be null.");
    Assert.notNull(user, "User cannot be null.");

    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);

    Goal goal = createGoal();
    bundle.addEntry(FhirUtil.createPostEntry(goal));

    return bundle;
  }

  private Goal createGoal() {
    Goal goal = new Goal();
    goal.getMeta()
        .addProfile(SDOHProfiles.GOAL);

    goal.setLifecycleStatus(Goal.GoalLifecycleStatus.ACTIVE);
    goal.setStatusDate(new Date());

    goal.setAchievementStatus(new CodeableConcept().addCoding(
        new Coding(achievementStatus.getSystem(), achievementStatus.toCode(), achievementStatus.getDisplay())));

    goal.addCategory()
        .addCoding(category);

    goal.setDescription(new CodeableConcept().addCoding(snomedCode));
    goal.getDescription()
        .setText(name);

    goal.setSubject(getPatientReference());
    goal.setStart(startDate);

    goal.setExpressedBy(FhirUtil.toReference(Patient.class, practitioner.getIdElement()
        .getIdPart(), practitioner.getNameFirstRep()
        .getNameAsSingleString()));

    if (!Strings.isNullOrEmpty(comment)) {
      goal.addNote()
          .setText(comment)
          .setTimeElement(DateTimeType.now())
          .setAuthor(new Reference(new IdType(user.getUserType(), user.getId())).setDisplay(user.getName()));
    }
    if (problems != null) {
      problems.forEach(problem -> goal.addAddresses(FhirUtil.toReference(Condition.class, problem.getIdElement()
          .getIdPart(), problem.getCode()
          .getCodingFirstRep()
          .getDisplay())));
    }
    return goal;
  }

  private Reference getPatientReference() {
    return FhirUtil.toReference(Patient.class, patient.getIdElement()
        .getIdPart(), patient.getNameFirstRep()
        .getNameAsSingleString());
  }
}
