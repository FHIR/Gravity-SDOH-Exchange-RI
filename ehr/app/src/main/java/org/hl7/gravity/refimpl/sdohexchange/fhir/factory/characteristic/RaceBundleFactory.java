package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicMethod;
import org.hl7.gravity.refimpl.sdohexchange.codes.DetailedRaceCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.RaceCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCorePersonalCharacteristicProfile;
import org.springframework.util.Assert;

@Getter
@Setter
public class RaceBundleFactory extends PersonalCharacteristicBundleFactory {

  private RaceCode[] values;
  private String[] detailedValues;
  private String description;

  public RaceBundleFactory(CharacteristicCode type, CharacteristicMethod method) {
    super(type, method);
  }

  @Override
  protected Observation createObservation() {
    Observation obs = super.createObservation();
    obs.getMeta()
        .addProfile(UsCorePersonalCharacteristicProfile.RACE);
    boolean valueIsSet = false;
    if (values != null && values.length != 0) {
      Assert.isTrue(values.length < 6, "Not more than 5 races are expected.");
      valueIsSet = true;
      Lists.newArrayList(values)
          .stream()
          .distinct()
          .forEach(v -> obs.addComponent()
              .setCode(new CodeableConcept(CharacteristicCode.RACE.toCoding()))
              .setValue(new CodeableConcept(v.toCoding())));
    }

    if (detailedValues != null && detailedValues.length != 0) {
      valueIsSet = true;
      Lists.newArrayList(detailedValues)
          .stream()
          .distinct()
          .forEach(v -> obs.addComponent()
              .setCode(new CodeableConcept(CharacteristicCode.RACE.toCoding()))
              .setValue(new CodeableConcept(DetailedRaceCode.toCoding(v))));
    }

    if (ObjectUtils.isNotEmpty(description)) {
      valueIsSet = true;
      obs.addComponent()
          .setCode(new CodeableConcept(CharacteristicCode.RACE.toCoding()))
          .setValue(new StringType(description));
    }
    Assert.isTrue(valueIsSet, "At least one of race, detailed race or description must be set.");
    return obs;
  }
}
