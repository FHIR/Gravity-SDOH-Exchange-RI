import { getTasks } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Task } from "@/types";

const ACTIVE_STATUSES = ["In Progress", "Received", "On Hold", "Accepted"];
const INACTIVE_STATUSES = ["Completed", "Cancelled", "Rejected", "Failed"];

export interface ITasks {
	tasks: Task[],
	lastSyncDate: string,
	isLoading: boolean
}

@Module({ dynamic: true, store, name: "tasks" })
class Tasks extends VuexModule implements ITasks {
	tasks: Task[] = [];
	lastSyncDate: string = "";
	isLoading: boolean = false;

	get activeRequests() {
		return this.tasks.filter((task: Task) => ACTIVE_STATUSES.includes(task.status));
	}

	get inactiveRequests() {
		return this.tasks.filter((task: Task) => INACTIVE_STATUSES.includes(task.status));
	}

	@Mutation
	setTasks(payload: Task[]) {
		this.tasks = payload;
	}

	@Mutation
	setLastSyncDate(payload: string) {
		this.lastSyncDate = payload;
	}

	@Mutation
	setIsLoading(payload: boolean) {
		this.isLoading = payload;
	}

	@Action
	async getTasks(): Promise<void> {
		this.setIsLoading(true);
		try {
			const data = await getTasks();

			this.setTasks(data);
			this.setLastSyncDate(new Date().toISOString());
		} finally {
			this.setIsLoading(false);
		}
	}
}

export const TasksModule = getModule(Tasks);
