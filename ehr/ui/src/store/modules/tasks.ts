import { getTasks } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { TaskResponse } from "@/types";

export interface ITasks {
	tasks: TaskResponse[] | null
}

@Module({ dynamic: true, store, name: "tasks" })
class Tasks extends VuexModule implements ITasks {
	tasks: TaskResponse[] | null = null;

	@Mutation
	setTasks(payload: TaskResponse[]): void {
		this.tasks = payload;
	}

	@Action
	async getTasks(): Promise<void> {
		const data = await getTasks();

		this.setTasks(data);
	}
}

export const TasksModule = getModule(Tasks);
