package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicMethod;
import org.hl7.gravity.refimpl.sdohexchange.codes.PersonalPronounsCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCorePersonalCharacteristicProfile;
import org.springframework.util.Assert;

@Getter
@Setter
public class PersonalPronounsBundleFactory extends PersonalCharacteristicBundleFactory {

  private final PersonalPronounsCode value;
  private String valueDetail;

  public PersonalPronounsBundleFactory(CharacteristicCode type, CharacteristicMethod method,
      PersonalPronounsCode value) {
    super(type, method);
    this.value = value;
  }

  @Override
  protected Observation createObservation() {
    Assert.notNull(value, "Value cannot be null.");

    Observation obs = super.createObservation();
    CodeableConcept codeableConcept = new CodeableConcept(value.toCoding());
    if (PersonalPronounsCode.OTHER.equals(value)) {
      Assert.hasLength(valueDetail, "Value detail cannot be empty if value is 'Other'.");
      codeableConcept.setText(valueDetail);
    }
    obs.setValue(codeableConcept)
        .getMeta()
        .addProfile(UsCorePersonalCharacteristicProfile.PERSONAL_PRONOUNS);
    return obs;
  }
}
