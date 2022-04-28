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
import org.hl7.gravity.refimpl.sdohexchange.codes.DetailedEthnicityCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.EthnicityCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCorePersonalCharacteristicProfile;
import org.springframework.util.Assert;

@Getter
@Setter
public class EthnicityBundleFactory extends PersonalCharacteristicBundleFactory {

  private EthnicityCode value;
  private String[] detailedValues;
  private String description;

  public EthnicityBundleFactory(CharacteristicCode type, CharacteristicMethod method) {
    super(type, method);
  }

  @Override
  protected Observation createObservation() {
    Observation obs = super.createObservation();
    obs.getMeta()
        .addProfile(UsCorePersonalCharacteristicProfile.ETHNICITY);
    boolean valueIsSet = false;
    if (value != null) {
      valueIsSet = true;
      obs.addComponent()
          .setCode(new CodeableConcept(CharacteristicCode.ETHNICITY.toCoding()))
          .setValue(new CodeableConcept(value.toCoding()));
    }

    if (detailedValues != null && detailedValues.length != 0) {
      valueIsSet = true;
      Lists.newArrayList(detailedValues)
          .stream()
          .distinct()
          .forEach(v -> obs.addComponent()
              .setCode(new CodeableConcept(CharacteristicCode.ETHNICITY.toCoding()))
              .setValue(new CodeableConcept(DetailedEthnicityCode.toCoding(v))));
    }

    if (ObjectUtils.isNotEmpty(description)) {
      valueIsSet = true;
      obs.addComponent()
          .setCode(new CodeableConcept(CharacteristicCode.ETHNICITY.toCoding()))
          .setValue(new StringType(description));
    }
    Assert.isTrue(valueIsSet, "At least one of ethnicity, detailed ethnicity or description must be set.");
    return obs;
  }
}
