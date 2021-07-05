import { Action, getModule, Module, Mutation, VuexModule } from "vuex-module-decorators";
import { getActiveConcerns, getResolvedConcerns, addConcernResponse } from "@/api";
import store from "@/store";
import { Concern, NewConcernPayload } from "@/types";

export interface IConcerns {
	activeConcerns: Concern[],
	resolvedConcerns: Concern[]
}

@Module({ dynamic: true, store, name: "concern" })
class Concerns extends VuexModule implements IConcerns {
	activeConcerns: Concern[] = [];
	resolvedConcerns: Concern[] = [];

	@Mutation
	setActiveConcerns(payload: Concern[]): void {
		this.activeConcerns = payload;
	}

	@Mutation
	setResolvedConcerns(payload: Concern[]): void {
		this.resolvedConcerns = payload;
	}

	@Mutation
	addConcern(payload: Concern): void {
		this.activeConcerns = [...this.activeConcerns, payload];
	}

	@Action
	async getActiveConcerns(): Promise<void> {
		const data = await getActiveConcerns();

		this.setActiveConcerns(data);
	}

	@Mutation
	removeConcernFormList(id: string) {
		this.concerns = this.concerns.filter(concern => concern.id !== id);
	}

	@Mutation
	promoteOrResolve(payload: Concern) {
		this.concerns = this.concerns.map(el => el.id === payload.id ? payload : el);
	}

	@Action
	async getResolvedConcerns(): Promise<void> {
		const data = await getResolvedConcerns();

		this.setResolvedConcerns(data);
	}

	@Action
	async createConcern(payload: NewConcernPayload): Promise<void> {
		const data = await addConcernResponse(payload);

		this.addConcern(data);
	}

	@Action
	removeConcern(id: string) {
		this.removeConcernFormList(id);
	}

	@Action
	promoteOrResolveConcern(concern: Concern) {
		this.promoteOrResolve(concern);
	}
}

export const ConcernsModule = getModule(Concerns);
