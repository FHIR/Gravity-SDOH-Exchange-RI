export type Patient = {
	address: string | null,
	age: number | null,
	dob: string | null,
	gender: string | null,
	id: string | null,
	name: string | null
	//todo: keys below we don't have in our api yet
	language: string | null,
	phone: string | null,
	email: string | null,
	employmentStatus: string | null,
	race: string | null,
	ethnicity: string | null,
	educationLevel: string | null,
	maritalStatus: string | null,
	insurance: string | null
};

export type User = {
	id: string | null,
	name: string | null,
	userType: string | null
};

export type ContextResponse = {
	patient: Patient,
	user: User
};

export type Organization = {
	errors: string[],
	name: string,
	organizationId: string,
	type: "CBO" | "CBRO"
};

export type ServiceRequest = {
	category: ServiceRequestCategory,
	details: string,
	errors: string[],
	//todo: on be it's enum right now
	request: string,
	serviceRequestId: string,
	status: ServiceRequestStatus
};

export type ServiceRequestStatus = "ACTIVE" | "COMPLETED" | "DRAFT" | "ENTEREDINERROR" | "NULL" | "ONHOLD" | "REVOKED" | "UNKNOWN"

export type ServiceRequestCategory = "EDUCATION_DOMAIN" | "EMPLOYMENT_DOMAIN" | "FINANCIAL_STRAIN_DOMAIN" | "FOOD_INSECURITY_DOMAIN" | "HOUSING_INSTABILITY_AND_HOMELESSNESS_DOMAIN" | "INADEQUATE_HOUSING_DOMAIN" | "INTERPERSONAL_VIOLENCE_DOMAIN" | "SDOH_RISK_RELATED_TO_VETERAN_STATUS" | "SOCIAL_ISOLATION_DOMAIN" | "STRESS_DOMAIN" | "TRANSPORTATION_INSECURITY_DOMAIN"

export type TaskResponse = {
	createdAt: string,
	errors: string[],
	lastModified: string | null,
	organization: Organization | null,
	outcome: string | null,
	serviceRequest: ServiceRequest,
	status: TaskStatus,
	taskId: string,
	type: TaskType
};

export type newTaskPayload = {
	category: string,
	conditionIds: string[],
	consent: boolean,
	comment: string,
	goalIds: string[],
	performerId: string,
	// request: string,
	name: string
};

export type TaskStatus = "ACCEPTED" | "CANCELLED" | "COMPLETED" | "DRAFT" | "ENTEREDINERROR" | "FAILED" | "INPROGRESS" | "NULL" | "ONHOLD" | "READY" | "RECEIVED" | "REJECTED" | "REQUESTED"

export type TaskType = "ABORT" | "APPROVE" | "CHANGE" | "FULFILL" | "NULL" | "REPLACE" | "RESUME" | "SUSPEND"

export type Condition = {
	clinicalStatus: "ACTIVE" | "INACTIVE" | "NULL" | "RESOLVED",
	conditionId: string,
	dateRecorded: string,
	//todo: it's enum in api
	domain: string,
	errors: string[],
	verificationStatus: "CONFIRMED" | "DIFFERENTIAL" | "ENTEREDINERROR" | "NULL" | "PROVISIONAL" | "REFUTED" | "UNCONFIRMED"
};

export type Goal = {
	achievementStatus: "ACHIEVED" | "IMPROVING" | "INPROGRESS" | "NOCHANGE" | "NOPROGRESS" | "NOTACHIEVED" | "NOTATTAINABLE" | "NULL" | "SUSTAINING" | "WORSENING",
	//todo: it's enum in api
	domain: string,
	errors: string[],
	goalId: string,
	lifecycleStatus: "ACCEPTED" | "ACTIVE" | "CANCELLED" | "COMPLETED" | "ENTEREDINERROR" | "NULL" | "ONHOLD" | "PLANNED" | "PROPOSED" | "REJECTED",
	statusDate: string
};
