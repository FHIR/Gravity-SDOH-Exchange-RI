import axios from "axios";
import { User, Task } from "@/types";


const dataOnly = <T>({ data }: { data: T }): T => data;


export const getUserInfo = () => axios.get<User>("/user-info").then(dataOnly);

export const getTasks = () => axios.get<Task[]>("/task").then(dataOnly);
