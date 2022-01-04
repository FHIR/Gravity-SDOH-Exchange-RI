import { createPatientTask, getPatientTask, getPatientTasks, updatePatientTask } from "@/api";
import { Action, getModule, Module, Mutation, VuexModule } from "vuex-module-decorators";
import store from "@/store";
import { NewPatientTaskPayload, PatientTask, UpdatePatientTaskPayload } from "@/types";

export interface IPatientTasks {
	patientTasks: PatientTask[]
}

@Module({ dynamic: true, store, name: "patientTasks" })
class PatientTasks extends VuexModule implements IPatientTasks {
	patientTasks: PatientTask[] = [];

	@Mutation
	setPatientTasks(payload: PatientTask[]): void {
		this.patientTasks = payload;
	}

	@Mutation
	changePatientTask(payload: PatientTask): void {
		this.patientTasks = this.patientTasks.map(t => t.id === payload.id ? payload : t);
	}

	@Action
	async getPatientTasks(): Promise<void> {
		const data = await getPatientTasks();

		this.setPatientTasks(data);
	}

	@Action
	async createPatientTask(payload: NewPatientTaskPayload): Promise<void> {
		await createPatientTask(payload);
		//todo: on create task we don't have newly created but just id, so we need to fetch new list
		await this.getPatientTasks();
	}

	@Action
	async updatePatientTask(payload: UpdatePatientTaskPayload): Promise<void> {
		const updatedTask = await updatePatientTask(payload);
		this.changePatientTask(updatedTask);
	}

	@Action
	async getPatientTask(id: string): Promise<PatientTask> {
		return await getPatientTask(id);
	}
}

export const PatientTasksModule = getModule(PatientTasks);
