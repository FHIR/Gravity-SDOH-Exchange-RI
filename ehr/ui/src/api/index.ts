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
	Goal,
	NewConcernPayload
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
	//todo: remove mock after BE sync
	concernStatus: "Active",
	name: "Hunger Vital Signs",
	assessmentDate: "2021-05-18T14:15:08",
	category: "Food Insecurity",
	basedOn: "Past",
	status: "send to patient"
}, {
	name: "Hunger Vital Signs",
	assessmentDate: "2021-05-18T14:15:08",
	category: "Food Insecurity",
	basedOn: "Past",
	status: "send to patient",
	concernStatus: "PromotedOrResolved"
}];

// TODO: Delete when BE will be ready
export const addConcernResponse = (payload: NewConcernPayload): Concern => ({
	name: payload.name,
	assessmentDate: payload.assessmentDate,
	category: payload.category,
	basedOn: payload.basedOn,
	status: payload.status,
	concernStatus: payload.concernStatus
});

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

//todo: remove mock after BE sync
export const getIcd10Codes = async (): Promise<Coding[]> => [{ display: "Transportation Insecurity", code: "Z59.82" }];
export const getSnomedCtCodes = async (): Promise<Coding[]> => [{ display: "Food Insecurity", code: "F19.12" }];

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
