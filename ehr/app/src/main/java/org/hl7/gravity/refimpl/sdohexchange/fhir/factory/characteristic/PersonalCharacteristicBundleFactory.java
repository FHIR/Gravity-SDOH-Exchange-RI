package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic;

import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicMethod;
import org.hl7.gravity.refimpl.sdohexchange.codes.SDOHTemporaryCode;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

@Getter
@Setter
@RequiredArgsConstructor
public class PersonalCharacteristicBundleFactory {

  private final CharacteristicCode type;
  private final CharacteristicMethod method;
  private String methodDetail;

  public final Bundle createBundle() {
    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);

    Observation observation = createObservation();
    bundle.addEntry(FhirUtil.createPostEntry(observation));

    return bundle;
  }

  protected Observation createObservation() {
    Assert.notNull(type, "Type cannot be null.");
    Assert.notNull(method, "Method cannot be null.");

    CodeableConcept codeableConcept = new CodeableConcept(method.toCoding());
    if (!CharacteristicMethod.SELF_REPORTED.equals(method)) {
      Assert.hasLength(methodDetail, "Method detail cannot be empty if type is 'Derived' or 'Other'.");
      codeableConcept.setText(methodDetail);
    }
    return new Observation().setSubject(FhirUtil.toReference(Patient.class, SmartOnFhirContext.get()
            .getPatient()))
        .addPerformer(new Reference(new IdType(SmartOnFhirContext.get()
            .getFhirUser()).toUnqualifiedVersionless()))
        .addCategory(new CodeableConcept(SDOHTemporaryCode.PERSONAL_CHARACTERISTIC.toCoding()))
        .setCode(new CodeableConcept(type.toCoding()))
        .setMethod(codeableConcept)
        .setStatus(Observation.ObservationStatus.FINAL)
        .setEffective(DateTimeType.now());
  }
}
