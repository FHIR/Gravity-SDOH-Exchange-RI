import axios from "axios";
import { ContextResponse, TaskResponse } from "@/types";

export const getContext = async (): Promise<ContextResponse> => {
	const res = await axios.get("/api/current-context");

	return res.data;
};

export const getTasks = async (): Promise<TaskResponse[]> => {
	const res = await axios.get("/api/task");

	return res.data;
};
