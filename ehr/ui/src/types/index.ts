export type Patient = {
	address: string | null,
	age: number | null,
	dob: string | null,
	gender: string | null,
	id: string | null,
	name: string | null
	language: string | null,
	phones: Phone[],
	emails: Email[],
	employmentStatus: string | null,
	race: string | null,
	ethnicity: string | null,
	education: string | null,
	maritalStatus: string | null,
	insurances: string[]
};

export type Email = {
	email: string,
	use: string
};

export type Phone = {
	phone: string,
	use: string
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

export type Task = {
	comments: Comment[],
	createdAt: string,
	errors: string[],
	id: string,
	lastModified: string | null,
	name: string,
	organization: Organization | null,
	outcome: string | null,
	priority: "ASAP" | "Routine" | "Urgent" | null,
	procedures: Procedure[]
	serviceRequest: ServiceRequest,
	status: TaskStatus,
	statusReason: string | null
};

export type Organization = {
	errors: string[],
	name: string,
	id: string,
	type: "CBO" | "CBRO"
};

export type Occurrence = {
	start?: string | null,
	end: string
}

export type ServiceRequestGoal = {
	display: string,
	id: string
}

export type ServiceRequestConsent = {
	display: string,
	id: string
}

export type Coding = {
	code: string,
	display: string
}

export type ServiceRequestCondition = {
	display: string,
	id: string
};

export type Procedure = {
	display: string,
	id: string
}

export type Period = {
	start: string,
	end?: string
}

export type ServiceRequest = {
	category: Coding,
	code: Coding,
	conditions: ServiceRequestCondition[],
	consent: ServiceRequestConsent,
	errors: string[],
	goals: ServiceRequestGoal[],
	id: string,
	occurrence: Occurrence
};

export type newTaskPayload = {
	category: string,
	conditionIds: string[],
	consent: boolean,
	comment: string,
	goalIds: string[],
	performerId: string,
	code: string,
	name: string,
	occurrence: Occurrence | string
};

export type NewConcernPayload = {
	name: string,
	category: string
	icdCode: string,
	snomedCode: string
}

export type Concern = {
	id: string,
	name: string,
	category: Coding,
	icdCode: Coding,
	snomedCode: Coding,
	basedOn: string | {
		display: string,
		id: string,
	},
	assessmentDate?: string,
	startDate?: string,
	resolutionDate?: string,
	errors: string[]
};

export type updateTaskPayload = {
	comment?: string,
	status: TaskStatus | null,
	id: string
}

export type TaskStatus = "Accepted" | "Cancelled" | "Completed" | "Draft" | "Entered In Error" | "Failed" | "In Progress" | "Null" | "On Hold" | "Ready" | "Received" | "Rejected" | "Requested"

export type Comment = {
	author: {
		display: string,
		id: string,
		resourceType: string
	},
	text: string,
	time: string
};

export type Assessment = {
	id: string,
	name: string,
	questionnaireUrl: string,
	date: string,
	healthConcerns: {
		id: string,
		display: string
	}[],
	previous?: Assessment[],
	assessmentResponse: {
		question: string,
		answer: string
	}[]
};

export type Problem = {
	id: string,
	name: string,
	authoredBy?: {},
	basedOn: string | {
		display: string,
		id: string,
	},
	category: Coding,
	assessmentDate?: string,
	startDate?: string,
	resolutionDate?: string,
	errors: string[],
	icdCode: Coding,
	snomedCode: Coding
};

export type newProblemPayload = {
	name: string,
	category: string,
	codeICD: string,
	codeSNOMED: string
}

export type updateProblemPayload = {
	id: string,
	closeDate: string,
	status: string
}

export type Goal = {
	id: string,
	name: string,
	problems: string[],
	addedBy: string,
	startDate: string,
	endDate: string,
	targets: string[],
	comments: Comment[],
	category: Coding,
	code: Coding,
	status: GoalStatus
};

export type NewGoalPayload = {
	name: string,
	category: string,
	code: string,
	problems?: string[],
	addedBy?: string,
	startDate?: string,
	comments?: string,
}

export type GoalStatus = "active" | "completed"

export type UpdateGoalPayload = {
	id: string,
	category?: string,
	code?: string,
	name?: string,
	problems?: string[],
	startDate?: string,
	addedBy?: string
	comment?: string,
	status?: "active" | "completed",
	endDate?: string
};

export type Consent = {
	id: string,
	name: string,
	status: string,
	scope: string,
	category: string,
	organization: string,
	consentDate: string
};
