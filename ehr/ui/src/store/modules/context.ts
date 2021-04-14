import { getContext } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Patient, User } from "@/types";

export interface IContext {
	patient: Patient | null,
	user: User | null
}

@Module({ dynamic: true, store, name: "context" })
class Context extends VuexModule implements IContext {
	patient: Patient | null = null;
	user: User | null = null;

	@Mutation
	setPatient(payload: Patient): void {
		this.patient = payload;
	}

	@Mutation
	setUser(payload: User): void {
		this.user = payload;
	}

	@Action
	async getContext(): Promise<void> {
		const { patient, user } = await getContext();

		this.setPatient(patient);
		this.setUser(user);
	}
}

export const ContextModule = getModule(Context);
