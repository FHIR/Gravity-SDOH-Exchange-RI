export type User = {
	id: string | null,
	name: string | null,
	preferredUsername: string | null,
	userType: string | null
}

export type TaskStatus = "ACCEPTED" | "CANCELLED" | "COMPLETED" | "DRAFT" | "ENTEREDINERROR" | "FAILED" | "INPROGRESS" | "NULL" | "ONHOLD" | "READY" | "RECEIVED" | "REJECTED" | "REQUESTED"

export type TaskPriority = "ASAP" | "NULL" | "ROUTINE" | "STAT" | "URGENT"

export type Comment = {
	author: {
		resourceType: string,
		id: string,
		display: string
	},
	time: string,
	text: string
}

export type Task = {
	id: string,
	name: string,
	createdAt: string,
	lastModified: string,
	priority: TaskPriority,
	status: TaskStatus,
	serviceRequest: {
		category: string,
		id: string
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
	outcome: string | null
}
