import { getTasks } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Task } from "@/types";

export interface ITasks {
	tasks: Task[]
}

@Module({ dynamic: true, store, name: "tasks" })
class Tasks extends VuexModule implements ITasks {
	tasks: Task[] = [];

	get activeRequests() {
		return this.tasks.filter((task: Task) => task.status !== "Completed" && task.status !== "Cancelled" && task.status !== "Failed" && task.status !== "Rejected");
	}

	get inactiveRequests() {
		return this.tasks.filter((task: Task) => task.status === "Completed" || task.status === "Cancelled" || task.status === "Rejected" || task.status === "Failed");
	}

	@Mutation
	setTasks(payload: Task[]) {
		this.tasks = payload;
	}

	@Action
	async getTasks(): Promise<void> {
		const data = await getTasks();

		this.setTasks(data);
	}
}

export const TasksModule = getModule(Tasks);
