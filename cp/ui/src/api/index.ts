import axios from "axios";
import { User, Task, UpdateTaskPayload, Procedure, Resources } from "@/types";


const dataOnly = <T>({ data }: { data: T }): T => data;


export const getUserInfo = () => axios.get<User>("/user-info").then(dataOnly);

export const getTasks = () => axios.get<Task[]>("/task").then(dataOnly);

export const updateTask = (taskId: string, data: UpdateTaskPayload) => axios.put<Task>(`/task/${taskId}`, data).then(dataOnly);

export const getProceduresForCategory = (categoryCode: string) => axios.get<Procedure[]>(`/mappings/categories/${categoryCode}/procedure/codings`).then(dataOnly);

export const getTaskResources = (taskId: string) => axios.get<Resources>(`/resources/task/${taskId}`).then(dataOnly);
