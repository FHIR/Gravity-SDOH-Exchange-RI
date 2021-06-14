import axios from "axios";
import {
	Coding,
	Concern,
	Condition,
	ContextResponse,
	Goal,
	newTaskPayload,
	Organization,
	Task,
	updateTaskPayload
} from "@/types";

export const getContext = async (): Promise<ContextResponse> => {
	const res = await axios.get("/current-context");

	return res.data;
};

export const getTasks = async (): Promise<Task[]> => {
	const res = await axios.get("/task");

	return res.data;
};

export const getConcerns = async (): Promise<Concern[]> => [{
	status: "Active",
	id: "123123",
	name: "Hunger Vital Signs",
	createdAt: "2021-05-18T14:15:08",
	category: "Food Insecurity",
	basedOn: "Past",
	actions: "send to patient"
}, {
	status: "PromotedOrResolved",
	id: "123123",
	name: "Hunger Vital Signs",
	createdAt: "2021-05-18T14:15:08",
	category: "Food Insecurity",
	basedOn: "Past",
	actions: "send to patient"
}];

export const createTask = async (payload: newTaskPayload): Promise<{ taskId: string }> => {
	const res = await axios.post("/task", payload);

	return res.data;
};

export const updateTask = async ({ id, ...data }: updateTaskPayload): Promise<Task> => {
	const res = await axios.put(`/task/${id}`, data);

	return res.data;
};

export const getConditions = async (): Promise<Condition[]> => {
	const res = await axios.get("/support/conditions");

	return res.data;
};

export const getGoals = async (): Promise<Goal[]> => {
	const res = await axios.get("/support/goals");

	return res.data;
};

export const getOrganizations = async (): Promise<Organization[]> => {
	const res = await axios.get("/support/organizations");

	return res.data;
};

export const getCategories = async (): Promise<Coding[]> => {
	const res = await axios.get("/mappings/categories");

	return res.data;
};

export const getCodes = async (): Promise<Coding[]> => [{ display: "Transportation Insecurity", code: "Z59.82" }];

export const getRequests = async (code: string): Promise<Coding[]> => {
	const res = await axios.get(`/mappings/categories/${code}/servicerequest/codings`);

	return res.data;
};
