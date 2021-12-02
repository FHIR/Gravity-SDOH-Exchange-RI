import axios from "axios";
import { User, Task, UpdateTaskPayload, Procedure, Resources, Cbo, UpdateOurTaskPayload } from "@/types";


const dataOnly = <T>({ data }: { data: T }): T => data;


export const getUserInfo = () => axios.get<User>("/user-info").then(dataOnly);

export const getTasks = () => axios.get<Task[]>("/task").then(dataOnly);

export const getOurTasks = () => axios.get<Task[]>("/our-task").then(dataOnly);

export const getTask = (taskId: string) => axios.get<Task>(`/task/${taskId}`).then(dataOnly);

export const getOurTask = (taskId: string) => axios.get<Task>(`/our-task/${taskId}`).then(dataOnly);

export const updateTask = (taskId: string, data: UpdateTaskPayload) => axios.put<void>(`/task/${taskId}`, data).then(dataOnly);

export const updateOurTask = (taskId: string, data: UpdateOurTaskPayload) => axios.put<void>(`/our-task/${taskId}`, data).then(dataOnly);

export const getProceduresForCategory = (categoryCode: string) => axios.get<Procedure[]>(`/mappings/categories/${categoryCode}/procedure/codings`).then(dataOnly);

export const getCBOList = () => axios.get<Cbo[]>("/support/organizations").then(dataOnly);

export const getTaskResources = (taskId: string) => axios.get<Resources>(`/resources/task/${taskId}`).then(dataOnly);
