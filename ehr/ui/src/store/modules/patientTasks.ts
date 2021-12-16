import { getPatientTasks, createPatientTask, updatePatientTask } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { PatientTask, NewPatientTaskPayload, UpdatePatientTaskPayload } from "@/types";

export interface IPatientTasks {
	tasks: PatientTask[]
}

@Module({ dynamic: true, store, name: "patientTasks" })
class PatientTasks extends VuexModule implements IPatientTasks {
	tasks: PatientTask[] = [];

	@Mutation
	setTasks(payload: PatientTask[]): void {
		this.tasks = payload;
	}

	@Mutation
	changeTask(payload: PatientTask): void {
		this.tasks = this.tasks.map(t => t.id === payload.id ? payload : t);
	}

	@Action
	async getTasks(): Promise<void> {
		const data = await getPatientTasks();

		this.setTasks(data);
	}

	@Action
	async createTask(payload: NewPatientTaskPayload): Promise<void> {
		await createPatientTask(payload);
		//todo: on create task we don't have newly created but just id, so we need to fetch new list
		await this.getTasks();
	}

	@Action
	async updateTask(payload: UpdatePatientTaskPayload): Promise<void> {
		const updatedTask = await updatePatientTask(payload);
		this.changeTask(updatedTask);
	}
}

export const PatientTasksModule = getModule(PatientTasks);
