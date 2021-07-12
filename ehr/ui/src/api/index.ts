import axios from "axios";
import {
	Assessment,
	Consent,
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
	Problem,
	newProblemPayload,
	NewConcernPayload,
	UpdateGoalPayload,
	NewGoalPayload
} from "@/types";

export const getContext = async (): Promise<ContextResponse> => {
	const res = await axios.get("/current-context");

	return res.data;
};

export const getTasks = async (): Promise<Task[]> => {
	const res = await axios.get("/task");

	return res.data;
};

export const getActiveConcerns = async (): Promise<Concern[]> => {
	const res = await axios.get("/health-concern/active");

	return res.data;
};

export const getResolvedConcerns = async (): Promise<Concern[]> => {
	const res = await axios.get("/health-concern/resolved");

	return res.data;
};

export const resolveConcern = async (id: string) => {
	await axios.put(`/health-concern/resolve/${id}`);
};

export const removeConcern = async (id: string) => {
	await axios.put(`/health-concern/remove/${id}`);
};

export const promoteConcern = async (id: string) => {
	await axios.put(`/health-concern/promote/${id}`);
};

export const addConcernResponse = async (payload: NewConcernPayload): Promise<Concern> => {
	const res = await axios.post("/health-concern", payload);

	return res.data;
};

export const getPastAssessments = async () => (await axios.get<Assessment[]>("/assessment/past")).data;

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

export const getConditionCodes = async (category: string): Promise<{ codings: Coding[], display: "ICD-10-CM" | "SNOMED CT" }[]> => {
	const res = await axios.get(`/mappings/categories/${category}/condition/codings`);
	return res.data;
};
export const getRequests = async (code: string): Promise<Coding[]> => {
	const res = await axios.get(`/mappings/categories/${code}/servicerequest/codings`);

	return res.data;
};

export const getGoals = async (): Promise<Goal[]> => {
	//todo: remove mock after BE sync
	const res: Goal[] = [{
		id: "1",
		name: "Reduce Medication Const",
		problems: ["Food Insecurity", "Food Security"],
		addedBy: "test",
		startDate: "2021-05-18T14:07:48",
		endDate: "",
		targets: ["fisrt", "second"],
		comments: [{
			author: {
				display: "",
				id: "",
				resourceType: ""
			},
			text: "Some comments to share",
			time: "2021-05-18T14:07:48"
		}, {
			author: {
				display: "",
				id: "",
				resourceType: ""
			},
			text: "Another loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong comment",
			time: "2021-05-18T14:07:48"
		}],
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
		id: "2",
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

//todo: remove mock
export const updateGoal = async ({ id }: UpdateGoalPayload): Promise<Goal> => {
	const mock: Goal = {
		id,
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
		status: "active"
	};

	return mock;
	// const res = await axios.put(`/goal/${id}`, data);
	//
	// return res.data;
};

// todo: change and remove mocked data after sync with BE
export const createGoal = async (payload: NewGoalPayload): Promise<Goal> => ({
	name: payload.name,
	id: "123",
	category: { code: payload.category, display: payload.category },
	code: { code: payload.code, display: payload.code },
	startDate: payload?.startDate || (new Date()).toDateString(),
	comments: [],
	endDate: "",
	targets: [],
	addedBy: payload?.addedBy || "",
	problems: payload?.problems || [],
	status: "active"
});
// const res = await axios.post("/goal", payload);
// return res.data;

export const getGoalCodes = async (code: string): Promise<Coding[]> => {
	// todo: call real request and remove mocked data
	// const res = await axios.get(`/mappings/categories/${code}/goals/codings`);
	//return res.data;

	const res: Coding[] = [{
		code: "385767005",
		display: "Meals on wheels provision education"
	}];

	return res;
};


export const getActiveProblems = async(): Promise<Problem[]> => {
	const res = await axios.get("/problem/active");
	return res.data;
};

export const getClosedProblems = async(): Promise<Problem[]> => {
	const res = await axios.get("/problem/closed");
	return res.data;
};

// todo: change and remove mocked data after sync with BE
export const createProblem = (payload: newProblemPayload): Problem => ({
	name: payload.name,
	id: "123",
	category: { code: payload.category, display: payload.category },
	icdCode: { code: payload.codeICD, display: payload.codeICD },
	snomedCode: { code: payload.codeSNOMED, display: payload.codeSNOMED },
	assessmentDate: (new Date()).toDateString(),
	basedOn: "Conversation with Patient",
	errors: []
});
// const res = await axios.post("/problem", payload);
// return res.data;

export const closeProblem = async (id: string): Promise<Problem> => {
	const res = await axios.put(`/problem/close/${id}`);
	return res.data;
};

export const getConsents = async () => (await axios.get<Consent[]>("/consent")).data;

export const createConsent = async (name: string, attachment: File) => {
	const formData = new FormData();
	formData.append("name", name);
	formData.append("attachment", attachment);
	const resp = await axios.post<Consent>("/consent", formData);
	return resp.data;
};

export const getConsentAttachment = async (consentId: string) => (await axios.get<Blob>(`/consent/${consentId}/attachment`, { responseType: "blob" })).data;
