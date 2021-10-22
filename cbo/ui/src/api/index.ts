import { Task, User, Server, NewServerPayload, UpdateServerPayload, Procedure, Resources } from "@/types";
import axios from "axios";

export const getContext = async (): Promise<User> => ({ id: "vidsmok4uVBobra", name: "Colin Brooks", userType: "CEO" });
// const dataOnly = <T>({ data }: { data: T }): T => data;
// export const getTasks = () => axios.get<Task[]>("/task").then(dataOnly);
// TODO: Mock data while we don't have BE
export const getTasks = async (): Promise<Task[]> => ([
	{
		id: "1",
		name: "Task number 1",
		createdAt: "2021-10-05T19:11:08",
		lastModified: "2021-10-05T19:11:08",
		priority: "high",
		status: "Accepted",
		requestType: "active",
		serviceRequest: {
			id: "12",
			occurrence: {
				end: "2021-10-05T19:11:08",
				start: "2021-10-05T19:11:08"
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
		consent: "yes",
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
		createdAt: "2021-10-05T19:11:08",
		lastModified: "2021-10-05T19:11:08",
		priority: "high",
		status: "Received",
		requestType: "active",
		serviceRequest: {
			id: "12",
			occurrence: {
				end: "2021-10-05T19:11:08",
				start: "2021-10-05T19:11:08"
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
		consent: "yes",
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
		createdAt: "2021-10-05T19:11:08",
		lastModified: "2021-10-05T19:11:08",
		priority: "high",
		status: "Received",
		requestType: "active",
		serviceRequest: {
			id: "12",
			occurrence: {
				end: "2021-10-05T19:11:08",
				start: "2021-10-05T19:11:08"
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
		consent: "no",
		comments: [
			{
				author: {
					resourceType: "dahjkhdk",
					id: "dasdasda",
					display: "asdasda"
				},
				time: "2021-10-05T19:11:08",
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
		createdAt: "2021-10-05T19:11:08",
		lastModified: "2021-10-05T19:11:08",
		priority: "high",
		status: "Completed",
		requestType: "inactive",
		serviceRequest: {
			id: "12",
			occurrence: {
				end: "2021-10-05T19:11:08",
				start: "2021-10-05T19:11:08"
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
		consent: "no",
		comments: [
			{
				author: {
					resourceType: "dahjkhdk",
					id: "dasdasda",
					display: "asdasda"
				},
				time: "2021-10-05T19:11:08",
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
		createdAt: "2021-10-05T19:11:08",
		lastModified: "2021-10-05T19:11:08",
		priority: "high",
		status: "Completed",
		requestType: "inactive",
		serviceRequest: {
			id: "12",
			occurrence: {
				end: "2021-10-05T19:11:08",
				start: "2021-10-05T19:11:08"
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
		consent: "yes",
		comments: [
			{
				author: {
					resourceType: "dahjkhdk",
					id: "dasdasda",
					display: "asdasda"
				},
				time: "2021-10-05T19:11:08",
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
		createdAt: "2021-10-05T19:11:08",
		lastModified: "2021-10-05T19:11:08",
		priority: "high",
		status: "Cancelled",
		requestType: "inactive",
		serviceRequest: {
			id: "12",
			occurrence: {
				end: "2021-10-05T19:11:08",
				start: "2021-10-05T19:11:08"
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
		consent: "yes",
		comments: [
			{
				author: {
					resourceType: "dahjkhdk",
					id: "dasdasda",
					display: "asdasda"
				},
				time: "123131231",
				text: "comment-1"
			},
			{
				author: {
					resourceType: "dahjkhdk",
					id: "dasdasda",
					display: "asdasda"
				},
				time: "123131231",
				text: "comment-2"
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
//TODO: commented while BE is not ready
// export const updateTask = (taskId: string, data: UpdateTaskPayload) => axios.put<void>(`/task/${taskId}`, data).then(dataOnly);

export const getProceduresForCategory = async (categoryCode: string): Promise<Procedure[]> => ([
	{
		display: "Procedure: 1",
		code: "123678"
	},
	{
		display: "Procedure: 2",
		code: "123224678"
	}
]);

export const getServers = async (): Promise<Server[]> => {
	const res = await axios.get("/servers");

	return res.data;
};

export const createServer = async (payload: NewServerPayload): Promise<Server> => {
	const res = await axios.post("/servers", payload);

	return res.data;
};

export const updateServer = async ({ id, ...data }: UpdateServerPayload): Promise<Server> => {
	const res = await axios.put(`/servers/${id}`, data);

	return res.data;
};

export const getTaskResources = async (taskId: string): Promise<Resources> => ({
	task: "",
	serviceRequest: "",
	requester: "",
	patient: "",
	consent: "",
	conditions: [],
	goals: [],
	procedures: []
});
