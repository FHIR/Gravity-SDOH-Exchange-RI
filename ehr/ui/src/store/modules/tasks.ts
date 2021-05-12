import { getTasks, createTask, updateTask } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Task, newTaskPayload, updateTaskPayload } from "@/types";

export interface ITasks {
	tasks: Task[]
}

@Module({ dynamic: true, store, name: "tasks" })
class Tasks extends VuexModule implements ITasks {
	tasks: Task[] = [];

	@Mutation
	setTasks(payload: Task[]): void {
		this.tasks = payload;
	}

	@Mutation
	changeTask(payload: Task): void {
		this.tasks = this.tasks.map(t => t.id === payload.id ? payload : t);
	}

	@Action
	async getTasks(): Promise<void> {
		const data = await getTasks();

		this.setTasks(data);
	}

	@Action
	async createTask(payload: newTaskPayload): Promise<void> {
		await createTask(payload);
		//todo: on create task we don't have newly created but just id, so we need to fetch new list
		await this.getTasks();
	}

	@Action
	async updateTask(payload: updateTaskPayload): Promise<void> {
		const updatedTask = await updateTask(payload);
		this.changeTask(updatedTask);
	}
}

export const TasksModule = getModule(Tasks);
