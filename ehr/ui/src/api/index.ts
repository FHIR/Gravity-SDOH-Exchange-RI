import axios from "axios";
import {
	Concern,
	ContextResponse,
	Task,
	ServiceRequestCondition,
	ServiceRequestGoal,
	Organization,
	newTaskPayload,
	updateTaskPayload,
	Coding,
	Goal
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

export const getServiceRequestConditions = async (): Promise<ServiceRequestCondition[]> => {
	const res = await axios.get("/support/conditions");

	return res.data;
};

export const getServiceRequestGoals = async (): Promise<ServiceRequestGoal[]> => {
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

export const getGoals = async(): Promise<Goal[]> => {
	//todo: remove mock after BE sync
	const res: Goal[] = [{
		name: "Reduce Medication Const",
		problems: ["Food Insecurity"],
		addedBy: "test",
		startDate: "2021-05-18T14:07:48",
		endDate: "",
		targets: ["fisrt", "second"],
		comments: [],
		category: {
			code: "111",
			display: "Food Insecurity"
		},
		code: {
			code: "10782290009",
			display: "Food Security"
		},
		status: "active"
	}, {
		name: "Reduce Medication Const",
		problems: ["Food Insecurity"],
		addedBy: "test",
		startDate: "2021-05-18T14:07:48",
		endDate: "2021-06-15T14:07:48",
		targets: ["fisrt", "second"],
		comments: [],
		category: {
			code: "111",
			display: "Food Insecurity"
		},
		code: {
			code: "10782290009",
			display: "Food Security"
		},
		status: "completed"
	}];

	return res;
};
