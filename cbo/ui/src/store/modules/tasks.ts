import { getTasks, updateTask, getTask } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Task, UpdateTaskPayload } from "@/types";

const ACTIVE_STATUSES = ["In Progress", "Received", "On Hold", "Accepted"];
const INACTIVE_STATUSES = ["Completed", "Cancelled", "Rejected", "Failed"];

export interface ITasks {
	tasks: Task[]
}

@Module({ dynamic: true, store, name: "tasks" })
class Tasks extends VuexModule implements ITasks {
	tasks: Task[] = [];

	get activeRequests() {
		const activeTasks = this.tasks.filter((task: Task) => ACTIVE_STATUSES.includes(task.status));

		return activeTasks.map((task: Task) => ({ task, isNew: false }));
	}

	get inactiveRequests() {
		const inactiveTask = this.tasks.filter((task: Task) => INACTIVE_STATUSES.includes(task.status));

		return inactiveTask.map((task: Task) => ({ task, isNew: false }));
	}

	@Mutation
	setTasks(payload: Task[]) {
		this.tasks = payload;
	}

	@Mutation
	changeTask(payload: Task) {
		this.tasks = this.tasks.map(task => task.id === payload.id ? payload : task);
	}

	@Action
	async getTasks(): Promise<void> {
		const data = await getTasks();

		this.setTasks(data);
	}

	@Action
	async updateTask(payload :UpdateTaskPayload): Promise<Task> {
		await updateTask(payload);
		const updatedTask = await getTask(payload.id, payload.serverId);
		this.changeTask(updatedTask);

		return updatedTask;
	}
}

export const TasksModule = getModule(Tasks);
