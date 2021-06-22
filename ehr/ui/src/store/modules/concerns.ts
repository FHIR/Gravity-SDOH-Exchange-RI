import { Action, getModule, Module, Mutation, VuexModule } from "vuex-module-decorators";
import { getConcerns, addConcernResponse } from "@/api";
import store from "@/store";
import { Concern, NewConcernPayload } from "@/types";

export interface IConcerns {
	concerns: Concern[]
}

@Module({ dynamic: true, store, name: "concern" })
class Concerns extends VuexModule implements IConcerns {
	concerns: Concern[] = [];

	@Mutation
	setConcerns(payload: Concern[]): void {
		this.concerns = payload;
	}

	@Mutation
	addConcern(payload: Concern): void {
		this.concerns = [...this.concerns, payload];
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
	async getConcerns(): Promise<void> {
		const data = await getConcerns();

		this.setConcerns(data);
	}

	@Action
	createConcern(payload: NewConcernPayload) {
		this.addConcern(addConcernResponse(payload));
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
