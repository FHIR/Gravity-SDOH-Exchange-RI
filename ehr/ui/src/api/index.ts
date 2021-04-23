import axios from "axios";
import { ContextResponse, TaskResponse, Condition, Goal, Organization } from "@/types";

export const getContext = async (): Promise<ContextResponse> => {
	const res = await axios.get("/api/current-context");

	return res.data;
};

export const getTasks = async (): Promise<TaskResponse[]> => {
	const res = await axios.get("/api/task");

	return res.data;
};

export const getConditions = async (): Promise<Condition[]> => {
	const res = await axios.get("/api/support/conditions");

	return res.data;
};

export const getGoals = async (): Promise<Goal[]> => {
	const res = await axios.get("/api/support/goals");

	return res.data;
};

export const getOrganizations = async (): Promise<Organization[]> => {
	const res = await axios.get("/api/support/organizations");

	return res.data;
};
