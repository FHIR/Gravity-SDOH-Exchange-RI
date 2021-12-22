package org.hl7.gravity.refimpl.sdohexchange.codes;

import com.google.common.base.Strings;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r4.model.Coding;

public enum PatientTaskCode {
  MAKE_CONTACT,
  REVIEW_MATERIAL,
  ADHOC,
  COMPLETE_QUESTIONNAIRE;

  public static PatientTaskCode fromCode(String codeString) throws FHIRException {
    if (Strings.isNullOrEmpty(codeString)) {
      return null;
    }
    if ("make-contact".equals(codeString)) {
      return MAKE_CONTACT;
    }
    if ("review-material".equals(codeString)) {
      return REVIEW_MATERIAL;
    }
    if ("adhoc".equals(codeString)) {
      return ADHOC;
    }
    if ("complete-questionnaire".equals(codeString)) {
      return COMPLETE_QUESTIONNAIRE;
    }
    throw new FHIRException("Unknown PatientTaskCode code '" + codeString + "'");
  }

  public Coding toCoding() {
    switch (this) {
      case MAKE_CONTACT:
        return new Coding(SDOHTemporaryCode.SYSTEM, "make-contact", "Make Contact");
      case REVIEW_MATERIAL:
        return new Coding(SDOHTemporaryCode.SYSTEM, "review-material", "Review Material");
      case ADHOC:
        return new Coding(SDOHTemporaryCode.SYSTEM, "adhoc", "Adhoc");
      case COMPLETE_QUESTIONNAIRE:
        return new Coding(SDCTemporaryCode.SYSTEM, "complete-questionnaire", "Complete Questionnaire");
      default:
        return null;
    }
  }
}