import { Task, User, Server, NewServerPayload, UpdateServerPayload, Procedure, Resources } from "@/types";
import axios from "axios";

export const getContext = async (): Promise<User> => ({ id: "vidsmok4uVBobra", name: "Colin Brooks", userType: "CEO" });
const dataOnly = <T>({ data }: { data: T }): T => data;
export const getTasks = () => axios.get<Task[]>("/tasks").then(dataOnly);

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

export const deleteServer = async (id: number): Promise<void> => {
	await axios.delete(`/servers/${id}`);
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
