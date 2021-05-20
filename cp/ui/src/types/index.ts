export type User = {
	id: string | null,
	name: string | null,
	preferredUsername: string | null,
	userType: string | null
}

export type TaskStatus =
	| "Received"
	| "Accepted"
	| "Rejected"
	| "In Progress"
	| "On Hold"
	| "Completed"
	| "Cancelled"

export type TaskPriority = "ASAP" | "ROUTINE" | "URGENT"

export type Comment = {
	author: {
		resourceType: string,
		id: string,
		display: string
	},
	time: string,
	text: string
}

export type Occurrence = {
	end: string,
	start?: string
}

export type Task = {
	id: string,
	name: string,
	createdAt: string,
	lastModified: string,
	priority: string,
	status: TaskStatus,
	serviceRequest: {
		id: string,
		occurrence: Occurrence,
		category: {
			code: string,
			display: string
		},
		code: {
			code: string,
			display: string
		}
	},
	requester: {
		resourceType: string,
		id: string,
		display: string
	},
	patient: {
		resourceType: string,
		id: string,
		display: string
	},
	consent: string,
	comments: Comment[],
	outcome: string | null,
	statusReason: string | null,
	procedures: {
		id: string
		display: string,
	}[]
}


export type UpdatedStatus =
	| "Accepted"
	| "In Progress"
	| "On Hold"
	| "Rejected"
	| "Completed"

export type UpdateTaskPayload = {
	comment?: string,
	outcome?: string,
	statusReason?: string,
	procedureCodes?: string[],
	status: UpdatedStatus
}


export type Procedure = {
	code: string,
	display: string
}


export type Resources = {
	task: string,
	serviceRequest: string,
	requester: string,
	patient: string,
	consent: string,
	conditions: string[],
	goals: string[]
}
