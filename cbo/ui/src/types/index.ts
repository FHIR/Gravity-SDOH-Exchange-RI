export type User = {
	id: string | null,
	name: string | null,
	userType: string | null
};

export type TaskStatus =
	| "Received"
	| "Accepted"
	| "Rejected"
	| "In Progress"
	| "On Hold"
	| "Completed"
	| "Cancelled";

export type Task = {
	id: string,
	name: string,
	createdAt: string,
	lastModified: string,
	priority: string,
	status: any,
	requestType: string,
	serverId: number,
	isNew?: boolean,
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

export type TaskWithState = {
	task: Task,
	isNew: boolean
}

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

export type UpdatedStatus =
	| "Accepted"
	| "In Progress"
	| "On Hold"
	| "Rejected"
	| "Cancelled"
	| "Completed"

export type UpdateTaskPayload = {
	id: string,
	comment?: string,
	outcome?: string,
	statusReason?: string,
	procedureCodes?: string[],
	serverId: number,
	status: UpdatedStatus
}

export type Procedure = {
	code: string,
	display: string
}

export type Server = {
	id: number,
	serverName: string,
	fhirServerUrl: string,
	authServerUrl: string,
	clientId: string,
	clientSecret: string,
	lastSyncDate: string
}

export type NewServerPayload = {
	serverName: string,
	fhirServerUrl: string,
	authServerUrl: string,
	clientId: string,
	clientSecret: string
}

export type UpdateServerPayload = {
	id: number,
	serverName: string,
	fhirServerUrl: string,
	authServerUrl: string,
	clientId: string,
	clientSecret: string
}

export type Resources = {
	task: string,
	serviceRequest: string,
	requester: string,
	patient: string,
	consent: string,
	conditions: string[],
	goals: string[],
	procedures: string[]
}
