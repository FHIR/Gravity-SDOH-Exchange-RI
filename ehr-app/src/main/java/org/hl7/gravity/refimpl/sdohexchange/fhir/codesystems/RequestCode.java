package org.hl7.gravity.refimpl.sdohexchange.fhir.codesystems;

import com.google.common.base.Strings;
import org.hl7.fhir.exceptions.FHIRException;

//Currently contains ONLY a test set of SNOMED CT codes.
public enum RequestCode {
  //Food Insecurity domain
  ASSESSMENT_OF_HEALTH_AND_SOCIAL_CARE_NEEDS,
  ASSESSMENT_OF_NUTRITIONAL_STATUS,
  COUNSELING_ABOUT_NUTRITION,
  MEALS_ON_WHEELS_PROVISION_EDUCATION,
  NUTRITION_EDUCATION,
  PATIENT_REFERRAL_TO_DIETITIAN,
  PROVISION_OF_FOOD,
  REFERRAL_TO_COMMUNITY_MEALS_SERVICE,
  REFERRAL_TO_SOCIAL_WORKER,
  //Housing Instability/Homelessness domain
  HOUSING_ASSESSMENT,
  REFERRAL_TO_HOUSING_SERVICE,
  //Transportation Insecurity domain
  TRANSPORTATION_CASE_MANAGEMENT;

  public static RequestCode fromCode(String codeString) throws FHIRException {
    if (Strings.isNullOrEmpty(codeString)) {
      return null;
    }
    if ("710824005".equals(codeString)) {
      return ASSESSMENT_OF_HEALTH_AND_SOCIAL_CARE_NEEDS;
    }
    if ("1759002".equals(codeString)) {
      return ASSESSMENT_OF_NUTRITIONAL_STATUS;
    }
    if ("441041000124100".equals(codeString)) {
      return COUNSELING_ABOUT_NUTRITION;
    }
    if ("385767005".equals(codeString)) {
      return MEALS_ON_WHEELS_PROVISION_EDUCATION;
    }
    if ("61310001".equals(codeString)) {
      return NUTRITION_EDUCATION;
    }
    if ("103699006".equals(codeString)) {
      return PATIENT_REFERRAL_TO_DIETITIAN;
    }
    if ("710925007".equals(codeString)) {
      return PROVISION_OF_FOOD;
    }
    if ("713109004".equals(codeString)) {
      return REFERRAL_TO_COMMUNITY_MEALS_SERVICE;
    }
    if ("308440001".equals(codeString)) {
      return REFERRAL_TO_SOCIAL_WORKER;
    }
    if ("225340009".equals(codeString)) {
      return HOUSING_ASSESSMENT;
    }
    if ("710911006".equals(codeString)) {
      return REFERRAL_TO_HOUSING_SERVICE;
    }
    if ("410365006".equals(codeString)) {
      return TRANSPORTATION_CASE_MANAGEMENT;
    }
    throw new FHIRException("Unknown RequestCode code '" + codeString + "'");
  }

  public String toCode() {
    switch (this) {
      case ASSESSMENT_OF_HEALTH_AND_SOCIAL_CARE_NEEDS:
        return "710824005";
      case ASSESSMENT_OF_NUTRITIONAL_STATUS:
        return "1759002";
      case COUNSELING_ABOUT_NUTRITION:
        return "441041000124100";
      case MEALS_ON_WHEELS_PROVISION_EDUCATION:
        return "385767005";
      case NUTRITION_EDUCATION:
        return "61310001";
      case PATIENT_REFERRAL_TO_DIETITIAN:
        return "103699006";
      case PROVISION_OF_FOOD:
        return "710925007";
      case REFERRAL_TO_COMMUNITY_MEALS_SERVICE:
        return "713109004";
      case REFERRAL_TO_SOCIAL_WORKER:
        return "308440001";
      case HOUSING_ASSESSMENT:
        return "225340009";
      case REFERRAL_TO_HOUSING_SERVICE:
        return "710911006";
      case TRANSPORTATION_CASE_MANAGEMENT:
        return "410365006";
      default:
        return "?";
    }
  }

  //For now all codes belong to a SNOMED CT code system. In future - codes from multiple code systems will be available.
  public String getSystem() {
    return "http://snomed.info/sct";
  }

  public String getDisplay() {
    switch (this) {
      case ASSESSMENT_OF_HEALTH_AND_SOCIAL_CARE_NEEDS:
        return "Assessment of health and social care needs (procedure)";
      case ASSESSMENT_OF_NUTRITIONAL_STATUS:
        return "Assessment of nutritional status (procedure)";
      case COUNSELING_ABOUT_NUTRITION:
        return "Counseling about nutrition (procedure)";
      case MEALS_ON_WHEELS_PROVISION_EDUCATION:
        return "Meals on wheels provision education (procedure)";
      case NUTRITION_EDUCATION:
        return "Nutrition education (procedure)";
      case PATIENT_REFERRAL_TO_DIETITIAN:
        return "Patient referral to dietitian (procedure)";
      case PROVISION_OF_FOOD:
        return "Provision of food (procedure)";
      case REFERRAL_TO_COMMUNITY_MEALS_SERVICE:
        return "Referral to community meals service (procedure)";
      case REFERRAL_TO_SOCIAL_WORKER:
        return "Referral to social worker (procedure)";
      case HOUSING_ASSESSMENT:
        return "Housing assessment (procedure)";
      case REFERRAL_TO_HOUSING_SERVICE:
        return "Referral to housing service (procedure)";
      case TRANSPORTATION_CASE_MANAGEMENT:
        return "Transportation case management (procedure)";
      default:
        return "?";
    }
  }

}