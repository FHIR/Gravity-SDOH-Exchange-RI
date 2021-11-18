import { Task, User, Server, NewServerPayload, UpdateServerPayload, Procedure, Resources, UpdateTaskPayload } from "@/types";
import axios from "axios";

export const getContext = async (): Promise<User> => ({ id: "vidsmok4uVBobra", name: "Colin Brooks", userType: "CEO" });

const dataOnly = <T>({ data }: { data: T }): T => data;

export const getTasks = async (): Promise<Task[]> => {
	const res = await axios.get<Task[]>("/tasks");

	return res.data;
};

export const getTask = (taskId: string, serverId: number) => axios.get<Task>(`/tasks/${serverId}/${taskId}`).then(dataOnly);

export const updateTask = async ({ id, ...rest }: UpdateTaskPayload) => axios.put<void>(`/tasks/${id}`, rest);

export const getProceduresForCategory = (categoryCode: string) => axios.get<Procedure[]>(`/mappings/categories/${categoryCode}/procedure/codings`).then(dataOnly);

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

export const getTaskResources = ({ serverId, taskId }: {serverId: number, taskId: string}) => axios.get<Resources>(`resources/${serverId}/task/${taskId}`).then(dataOnly);
