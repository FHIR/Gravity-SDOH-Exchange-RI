import { Task, User } from "@/types";

export const getContext = async (): Promise<User> => ({ id: "vidsmok4uVBobra", name: "Colin Brooks", userType: "CEO" });
const dataOnly = <T>({ data }: { data: T }): T => data;
// export const getTasks = () => axios.get<Task[]>("/task").then(dataOnly);
// TODO: Mock data while we don't have BE
export const getTasks = async (): Promise<Task[]> => ([
	{
		id: "1",
		name: "Task number 1",
		createdAt: "29.01.1987",
		lastModified: "12.12.1234",
		priority: "high",
		status: "Accepted",
		requestType: "active",
		serviceRequest: {
			id: "12",
			occurrence: {
				end: "12313",
				start: "123131"
			},
			category: {
				code: "1231",
				display: "Nutrition"
			},
			code: {
				code: "code",
				display: "Bybluk"
			}
		},
		requester: {
			resourceType: "1231",
			id: "13131",
			display: "131313"
		},
		patient: {
			resourceType: "2we2f",
			id: "werwrw",
			display: "Britney Spears"
		},
		consent: "consent",
		comments: [
			{
				author: {
					resourceType: "dahjkhdk",
					id: "dasdasda",
					display: "asdasda"
				},
				time: "123131231",
				text: "31231313"
			}
		],
		outcome: "outcome text",
		statusReason: "Status reason text",
		procedures: [{
			id: "131231",
			display: "1312313"
		}]
	},
	{
		id: "2",
		name: "Task number 2",
		createdAt: "29.01.1987",
		lastModified: "12.12.1234",
		priority: "high",
		status: "Received",
		requestType: "active",
		serviceRequest: {
			id: "12",
			occurrence: {
				end: "12313",
				start: "123131"
			},
			category: {
				code: "1231",
				display: "Nutrition"
			},
			code: {
				code: "code",
				display: "Bybluk"
			}
		},
		requester: {
			resourceType: "1231",
			id: "13131",
			display: "131313"
		},
		patient: {
			resourceType: "2we2f",
			id: "werwrw",
			display: "Supers Racca"
		},
		consent: "consent",
		comments: [
			{
				author: {
					resourceType: "dahjkhdk",
					id: "dasdasda",
					display: "asdasda"
				},
				time: "123131231",
				text: "31231313"
			}
		],
		outcome: "outcome text",
		statusReason: "Status reason text",
		procedures: [{
			id: "131231",
			display: "1312313"
		}]
	},
	{
		id: "3",
		name: "Task number 3",
		createdAt: "29.01.1987",
		lastModified: "12.12.1234",
		priority: "high",
		status: "Received",
		requestType: "active",
		serviceRequest: {
			id: "12",
			occurrence: {
				end: "12313",
				start: "123131"
			},
			category: {
				code: "1231",
				display: "Nutrition"
			},
			code: {
				code: "code",
				display: "Bybluk"
			}
		},
		requester: {
			resourceType: "1231",
			id: "13131",
			display: "131313"
		},
		patient: {
			resourceType: "2we2f",
			id: "werwrw",
			display: "werwrwerw"
		},
		consent: "consent",
		comments: [
			{
				author: {
					resourceType: "dahjkhdk",
					id: "dasdasda",
					display: "asdasda"
				},
				time: "123131231",
				text: "31231313"
			}
		],
		outcome: "outcome text",
		statusReason: "Status reason text",
		procedures: [{
			id: "131231",
			display: "1312313"
		}]
	},
	{
		id: "4",
		name: "Task number 4",
		createdAt: "29.01.1987",
		lastModified: "12.12.1234",
		priority: "high",
		status: "Received",
		requestType: "inactive",
		serviceRequest: {
			id: "12",
			occurrence: {
				end: "12313",
				start: "123131"
			},
			category: {
				code: "1231",
				display: "Nutrition"
			},
			code: {
				code: "code",
				display: "Bybluk"
			}
		},
		requester: {
			resourceType: "1231",
			id: "13131",
			display: "131313"
		},
		patient: {
			resourceType: "2we2f",
			id: "werwrw",
			display: "werwrwerw"
		},
		consent: "consent",
		comments: [
			{
				author: {
					resourceType: "dahjkhdk",
					id: "dasdasda",
					display: "asdasda"
				},
				time: "123131231",
				text: "31231313"
			}
		],
		outcome: "outcome text",
		statusReason: "Status reason text",
		procedures: [{
			id: "131231",
			display: "1312313"
		}]
	},
	{
		id: "5",
		name: "Task number 5",
		createdAt: "29.01.1987",
		lastModified: "12.12.1234",
		priority: "high",
		status: "Received",
		requestType: "inactive",
		serviceRequest: {
			id: "12",
			occurrence: {
				end: "12313",
				start: "123131"
			},
			category: {
				code: "1231",
				display: "Nutrition"
			},
			code: {
				code: "code",
				display: "Bybluk"
			}
		},
		requester: {
			resourceType: "1231",
			id: "13131",
			display: "131313"
		},
		patient: {
			resourceType: "2we2f",
			id: "werwrw",
			display: "werwrwerw"
		},
		consent: "consent",
		comments: [
			{
				author: {
					resourceType: "dahjkhdk",
					id: "dasdasda",
					display: "asdasda"
				},
				time: "123131231",
				text: "31231313"
			}
		],
		outcome: "outcome text",
		statusReason: "Status reason text",
		procedures: [{
			id: "131231",
			display: "1312313"
		}]
	},
	{
		id: "6",
		name: "Task number 6",
		createdAt: "29.01.1987",
		lastModified: "12.12.1234",
		priority: "high",
		status: "Received",
		requestType: "inactive",
		serviceRequest: {
			id: "12",
			occurrence: {
				end: "12313",
				start: "123131"
			},
			category: {
				code: "1231",
				display: "Nutrition"
			},
			code: {
				code: "code",
				display: "Bybluk"
			}
		},
		requester: {
			resourceType: "1231",
			id: "13131",
			display: "131313"
		},
		patient: {
			resourceType: "2we2f",
			id: "werwrw",
			display: "werwrwerw"
		},
		consent: "consent",
		comments: [
			{
				author: {
					resourceType: "dahjkhdk",
					id: "dasdasda",
					display: "asdasda"
				},
				time: "123131231",
				text: "31231313"
			}
		],
		outcome: "outcome text",
		statusReason: "Status reason text",
		procedures: [{
			id: "131231",
			display: "1312313"
		}]
	}
]);

