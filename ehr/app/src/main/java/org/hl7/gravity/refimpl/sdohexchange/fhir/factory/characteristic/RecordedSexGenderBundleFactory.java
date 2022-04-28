package org.hl7.gravity.refimpl.sdohexchange.fhir.factory.characteristic;

import com.healthlx.smartonfhir.core.SmartOnFhirContext;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicCode;
import org.hl7.gravity.refimpl.sdohexchange.codes.CharacteristicMethod;
import org.hl7.gravity.refimpl.sdohexchange.codes.SexGenderCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCorePersonalCharacteristicProfile;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import java.util.Date;

@Getter
@Setter
public class RecordedSexGenderBundleFactory extends PersonalCharacteristicBundleFactory {

  private final SexGenderCode value;
  private String valueDetail;
  private String derivedFromFileName;
  private byte[] derivedFromFile;

  public RecordedSexGenderBundleFactory(CharacteristicCode type, CharacteristicMethod method, SexGenderCode value) {
    super(type, method);
    this.value = value;
  }

  @Override
  protected Observation createObservation() {
    Assert.notNull(value, "Value cannot be null.");
    Assert.hasLength(derivedFromFileName, "Derived From file name cannot be empty.");
    Assert.notNull(derivedFromFile, "Derived From file content must be set.");
    Assert.isTrue(CharacteristicMethod.DERIVED_SPECIFY.equals(getMethod()),
        "Only Derived method is expected for this type of characteristic.");

    Observation obs = super.createObservation();
    CodeableConcept codeableConcept = new CodeableConcept(value.toCoding());
    if (SexGenderCode.OTHER.equals(value)) {
      Assert.hasLength(valueDetail, "Value detail cannot be empty if value is 'Other'.");
      codeableConcept.setText(valueDetail);
    }
    obs.setValue(codeableConcept)
        .getMeta()
        .addProfile(UsCorePersonalCharacteristicProfile.SEX_GENDER);

    return obs;
  }

  @Override
  protected void onBundleCreated(Bundle bundle) {
    Observation obs = (Observation) bundle.getEntry()
        .get(0)
        .getResource();
    DocumentReference docRef = new DocumentReference().setStatus(Enumerations.DocumentReferenceStatus.CURRENT)
        .setDate(new Date());
    docRef.addAuthor(new Reference(new IdType(SmartOnFhirContext.get()
        .getFhirUser()).toUnqualifiedVersionless()));
    docRef.addContent()
        .setAttachment(new Attachment().setTitle(derivedFromFileName)
            .setContentType(MediaType.APPLICATION_PDF_VALUE)
            .setData(derivedFromFile));
    docRef.setId(IdType.newRandomUuid());
    Bundle.BundleEntryComponent postEntry = FhirUtil.createPostEntry(docRef);
    obs.addDerivedFrom(FhirUtil.toReference(DocumentReference.class, docRef.getIdElement()
        .getIdPart()));
    bundle.addEntry(postEntry);
  }

  @Override
  protected void assertMethodDetail() {
    //do nothing.
  }
}
