import axios from "axios";
import { ContextResponse } from "@/types";

export const getContext = async (): Promise<ContextResponse> => {
	const res = await axios.get("/api/current-context");

	return res.data;
};
