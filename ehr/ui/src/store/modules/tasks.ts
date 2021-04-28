import { getTasks, createTask } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Task, newTaskPayload } from "@/types";

export interface ITasks {
	tasks: Task[] | null
}

@Module({ dynamic: true, store, name: "tasks" })
class Tasks extends VuexModule implements ITasks {
	tasks: Task[] | null = null;

	@Mutation
	setTasks(payload: Task[]): void {
		this.tasks = payload;
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
}

export const TasksModule = getModule(Tasks);
