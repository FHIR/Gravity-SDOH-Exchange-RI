import { getTasks, updateTask, getTask } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Task, TaskWithState, UpdateTaskPayload } from "@/types";
import { poll } from "@/utils";

export const ACTIVE_STATUSES = ["In Progress", "Received", "On Hold", "Accepted"];
export const INACTIVE_STATUSES = ["Completed", "Cancelled", "Rejected", "Failed"];

const findUpdates = (newList: Task[], oldTasks: TaskWithState[]): { name: string, oldStatus: string, newStatus: string }[] =>
	newList.flatMap(task => {
		const existingTask = oldTasks.find(ts => ts.task.id === task.id);
		if (!existingTask) {

			return [];
		}
		const oldStatus = existingTask.task.status;
		const newStatus = task.status;
		if (oldStatus === newStatus) {

			return [];
		}

		return [{
			name: task.name,
			oldStatus,
			newStatus
		}];
	});

export interface ITasks {
	tasks: TaskWithState[],
	lastSyncDate: string,
	isLoading: boolean
}

@Module({ dynamic: true, store, name: "tasks" })
class Tasks extends VuexModule implements ITasks {
	tasks: TaskWithState[] = [];
	lastSyncDate: string = "";
	isLoading: boolean = false;

	get activeRequests() {
		return this.tasks.filter(({ task }) => ACTIVE_STATUSES.includes(task.status));
	}

	get inactiveRequests() {
		return this.tasks.filter(({ task }) => INACTIVE_STATUSES.includes(task.status));
	}

	@Mutation
	setTasks(payload: TaskWithState[]) {
		this.tasks = payload;
	}

	@Mutation
	changeTask(payload: Task) {
		this.tasks = this.tasks.map(task => task.task.id === payload.id ? { isNew: task.isNew, task: payload } : task);
	}

	@Mutation
	setLastSyncDate(payload: string) {
		this.lastSyncDate = payload;
	}

	@Mutation
	setIsLoading(payload: boolean) {
		this.isLoading = payload;
	}

	@Mutation
	markTaskAsNotNew(taskId: string) {
		this.tasks = this.tasks.map(taskState => taskState.task.id === taskId ? { ...taskState, isNew: false } : taskState);
	}

	@Action
	updateTasks(newList: Task[]) {
		const isTaskNew = (task: Task) => {
			const existingTask = this.tasks.find(ts => ts.task.id === task.id);
			return existingTask === undefined || existingTask.isNew;
		};

		this.setTasks(newList.map(task => ({
			task,
			isNew: isTaskNew(task)
		})));
	}

	@Action
	async getTasks(): Promise<void> {
		this.setIsLoading(true);

		try {
			const data = await getTasks();

			this.setTasks(data.map(task => ({ task, isNew: false })));
			this.setLastSyncDate(new Date().toISOString());
		} finally {
			this.setIsLoading(false);
		}
	}

	@Action
	async startPolling(onUpdates: (updates: { name: string, oldStatus: string, newStatus: string }[]) => void) {

		await this.getTasks();

		await poll(() => getTasks(), newList => {
			onUpdates(findUpdates(newList, this.tasks));
			this.updateTasks(newList);

			return true;
		}, 5000);
	}

	@Action
	async updateTask(payload :UpdateTaskPayload): Promise<void> {
		await updateTask(payload);
		const updatedTask = await getTask(payload.id, payload.serverId);
		this.changeTask(updatedTask);
	}
}

export const TasksModule = getModule(Tasks);
