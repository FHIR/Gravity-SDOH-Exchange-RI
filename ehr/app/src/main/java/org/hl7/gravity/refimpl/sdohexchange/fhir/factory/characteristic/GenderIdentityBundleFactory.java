package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic;

import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicMethod;
import org.hl7.gravity.refimpl.sdohexchange.codes.GenderIdentityCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCorePersonalCharacteristicProfile;
import org.springframework.util.Assert;

@Getter
@Setter
public class GenderIdentityBundleFactory extends PersonalCharacteristicBundleFactory {

  private final GenderIdentityCode value;
  private String valueDetail;

  public GenderIdentityBundleFactory(CharacteristicCode type, CharacteristicMethod method, GenderIdentityCode value) {
    super(type, method);
    this.value = value;
  }

  @Override
  protected Observation createObservation() {
    Assert.notNull(value, "Value cannot be null.");

    Observation obs = super.createObservation();
    CodeableConcept codeableConcept = new CodeableConcept(value.toCoding());
    if (GenderIdentityCode.OTHER.equals(value)) {
      Assert.hasLength(valueDetail, "Value detail cannot be empty if value is 'Other'.");
      codeableConcept.setText(valueDetail);
    }
    obs.setValue(codeableConcept)
        .getMeta()
        .addProfile(UsCorePersonalCharacteristicProfile.GENDER_IDENTITY);
    return obs;
  }
}
