import axios from "axios";
import { ContextResponse, TaskResponse, Condition, Goal, Organization, newTaskPayload } from "@/types";

export const getContext = async (): Promise<ContextResponse> => {
	const res = await axios.get("/current-context");

	return res.data;
};

export const getTasks = async (): Promise<TaskResponse[]> => {
	const res = await axios.get("/task");

	return res.data;
};

export const createTask = async (payload: newTaskPayload): Promise<{ taskId: string }> => {
	const res = await axios.post("/task", payload);

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
