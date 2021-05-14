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

export type Goal = {
	display: string,
	id: string
}

export type Consent = {
	display: string,
	id: string
}

export type Coding = {
	code: string,
	display: string
}

export type Condition = {
	display: string,
	id: string
};

export type Procedure = {
	display: string,
	id: string
}

export type ServiceRequest = {
	category: Coding,
	code: Coding,
	conditions: Condition[],
	consent: Consent,
	errors: string[],
	goals: Goal[],
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
