// hardcoded category/domains and request items that we support right now
export type CategoryListItem = {
	value: string,
	name: string
};

export const categoryList: CategoryListItem[] = [{
	value: "FOOD_INSECURITY_DOMAIN",
	name: "Food Insecurity Domain"
}, {
	value: "HOUSING_INSTABILITY_AND_HOMELESSNESS_DOMAIN",
	name: "Housing Instability and Homelessness Domain"
}, {
	value: "TRANSPORTATION_INSECURITY_DOMAIN",
	name: "Transportation Insecurity Domain"
}];

export type RequestListItem = {
	value: string,
	name: string,
	domain: string,
	code: string,
};

export const requestList: RequestListItem[] = [{
	value: "ASSESSMENT_OF_HEALTH_AND_SOCIAL_CARE_NEEDS",
	name: "Assessment of health and social care needs",
	domain: "FOOD_INSECURITY_DOMAIN",
	code: "710824005"
}, {
	value: "ASSESSMENT_OF_NUTRITIONAL_STATUS",
	name: "Assessment of nutritional status",
	domain: "FOOD_INSECURITY_DOMAIN",
	code: "1759002"
}, {
	value: "COUNSELING_ABOUT_NUTRITION",
	name: "Counseling about nutrition",
	domain: "FOOD_INSECURITY_DOMAIN",
	code: "441041000124100"
}, {
	value: "MEALS_ON_WHEELS_PROVISION_EDUCATION",
	name: "Meals on wheels provision education",
	domain: "FOOD_INSECURITY_DOMAIN",
	code: "385767005"
}, {
	value: "NUTRITION_EDUCATION",
	name: "Nutrition education",
	domain: "FOOD_INSECURITY_DOMAIN",
	code: "61310001"
}, {
	value: "PATIENT_REFERRAL_TO_DIETITIAN",
	name: "Patient referral to dietitian",
	domain: "FOOD_INSECURITY_DOMAIN",
	code: "103699006"
}, {
	value: "PROVISION_OF_FOOD",
	name: "Provision of food",
	domain: "FOOD_INSECURITY_DOMAIN",
	code: "710925007"
}, {
	value: "REFERRAL_TO_COMMUNITY_MEALS_SERVICE",
	name: "Referral to community meals service",
	domain: "FOOD_INSECURITY_DOMAIN",
	code: "713109004"
}, {
	value: "REFERRAL_TO_SOCIAL_WORKER",
	name: "Referral to social worker",
	domain: "FOOD_INSECURITY_DOMAIN",
	code: "308440001"
}, {
	value: "HOUSING_ASSESSMENT",
	name: "Housing assessment",
	domain: "HOUSING_INSTABILITY_AND_HOMELESSNESS_DOMAIN",
	code: "225340009"
}, {
	value: "REFERRAL_TO_HOUSING_SERVICE",
	name: "Referral to housing service",
	domain: "HOUSING_INSTABILITY_AND_HOMELESSNESS_DOMAIN",
	code: "710911006"
}, {
	value: "TRANSPORTATION_CASE_MANAGEMENT",
	name: "Transportation case management",
	domain: "TRANSPORTATION_INSECURITY_DOMAIN",
	code: "410365006"
}];
