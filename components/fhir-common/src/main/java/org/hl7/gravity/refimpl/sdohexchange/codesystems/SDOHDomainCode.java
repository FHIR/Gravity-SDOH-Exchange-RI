package org.hl7.gravity.refimpl.sdohexchange.codesystems;

import com.google.common.base.Strings;
import org.hl7.fhir.exceptions.FHIRException;

public enum SDOHDomainCode {
  FOOD_INSECURITY_DOMAIN,
  HOUSING_INSTABILITY_AND_HOMELESSNESS_DOMAIN,
  INADEQUATE_HOUSING_DOMAIN,
  TRANSPORTATION_INSECURITY_DOMAIN,
  FINANCIAL_STRAIN_DOMAIN,
  SOCIAL_ISOLATION_DOMAIN,
  STRESS_DOMAIN,
  INTERPERSONAL_VIOLENCE_DOMAIN,
  EDUCATION_DOMAIN,
  EMPLOYMENT_DOMAIN,
  SDOH_RISK_RELATED_TO_VETERAN_STATUS;

  public static final String SYSTEM = "http://hl7.org/fhir/us/sdoh-clinicalcare/CodeSystem/sdohcc-temporary-codes";

  public static SDOHDomainCode fromCode(String codeString) throws FHIRException {
    if (Strings.isNullOrEmpty(codeString)) {
      return null;
    }
    if ("food-insecurity-domain".equals(codeString)) {
      return FOOD_INSECURITY_DOMAIN;
    }
    if ("housing-instability-and-homelessness-domain".equals(codeString)) {
      return HOUSING_INSTABILITY_AND_HOMELESSNESS_DOMAIN;
    }
    if ("inadequate-housing-domain".equals(codeString)) {
      return INADEQUATE_HOUSING_DOMAIN;
    }
    if ("transportation-insecurity-domain".equals(codeString)) {
      return TRANSPORTATION_INSECURITY_DOMAIN;
    }
    if ("financial-strain-domain".equals(codeString)) {
      return FINANCIAL_STRAIN_DOMAIN;
    }
    if ("social-isolation-domain".equals(codeString)) {
      return SOCIAL_ISOLATION_DOMAIN;
    }
    if ("stress-domain".equals(codeString)) {
      return STRESS_DOMAIN;
    }
    if ("interpersonal-violence-domain".equals(codeString)) {
      return INTERPERSONAL_VIOLENCE_DOMAIN;
    }
    if ("education-domain".equals(codeString)) {
      return EDUCATION_DOMAIN;
    }
    if ("employment-domain".equals(codeString)) {
      return EMPLOYMENT_DOMAIN;
    }
    if ("sdoh-risk-related-to-veteran-status".equals(codeString)) {
      return SDOH_RISK_RELATED_TO_VETERAN_STATUS;
    }
    throw new FHIRException("Unknown SDOHDomainCode code '" + codeString + "'");
  }

  public String toCode() {
    switch (this) {
      case FOOD_INSECURITY_DOMAIN:
        return "food-insecurity-domain";
      case HOUSING_INSTABILITY_AND_HOMELESSNESS_DOMAIN:
        return "housing-instability-and-homelessness-domain";
      case INADEQUATE_HOUSING_DOMAIN:
        return "inadequate-housing-domain";
      case TRANSPORTATION_INSECURITY_DOMAIN:
        return "transportation-insecurity-domain";
      case FINANCIAL_STRAIN_DOMAIN:
        return "financial-strain-domain";
      case SOCIAL_ISOLATION_DOMAIN:
        return "social-isolation-domain";
      case STRESS_DOMAIN:
        return "stress-domain";
      case INTERPERSONAL_VIOLENCE_DOMAIN:
        return "interpersonal-violence-domain";
      case EDUCATION_DOMAIN:
        return "education-domain";
      case EMPLOYMENT_DOMAIN:
        return "employment-domain";
      case SDOH_RISK_RELATED_TO_VETERAN_STATUS:
        return "sdoh-risk-related-to-veteran-status";
      default:
        return "?";
    }
  }

  public String getSystem() {
    return SYSTEM;
  }

  public String getDisplay() {
    switch (this) {
      case FOOD_INSECURITY_DOMAIN:
        return "Food Insecurity Domain";
      case HOUSING_INSTABILITY_AND_HOMELESSNESS_DOMAIN:
        return "Housing Instability and Homelessness Domain";
      case INADEQUATE_HOUSING_DOMAIN:
        return "Inadequate Housing Domain";
      case TRANSPORTATION_INSECURITY_DOMAIN:
        return "Transportation Insecurity Domain";
      case FINANCIAL_STRAIN_DOMAIN:
        return "Financial Strain Domain";
      case SOCIAL_ISOLATION_DOMAIN:
        return "Social Isolation Domain";
      case STRESS_DOMAIN:
        return "Stress-Domain";
      case INTERPERSONAL_VIOLENCE_DOMAIN:
        return "Interpersonal Violence Domain";
      case EDUCATION_DOMAIN:
        return "Education-Domain";
      case EMPLOYMENT_DOMAIN:
        return "Employment Domain";
      case SDOH_RISK_RELATED_TO_VETERAN_STATUS:
        return "SDOH Risk Related to Veteran Status";
      default:
        return "?";
    }
  }

}