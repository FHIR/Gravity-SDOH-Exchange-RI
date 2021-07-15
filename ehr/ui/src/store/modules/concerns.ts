import { Action, getModule, Module, Mutation, VuexModule } from "vuex-module-decorators";
import { getActiveConcerns, getResolvedConcerns, addConcernResponse, resolveConcern, promoteConcern, removeConcern } from "@/api";
import store from "@/store";
import { Concern, NewConcernPayload } from "@/types";
import { ProblemsModule } from "@/store/modules/problems";

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
		this.activeConcerns = [payload, ...this.activeConcerns];
	}

	@Action
	async getActiveConcerns(): Promise<void> {
		const data = await getActiveConcerns();

		this.setActiveConcerns(data);
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
	async removeConcern(id: string) {
		await removeConcern(id);
		await this.getActiveConcerns();
	}

	@Action
	async resolveConcern(id: string): Promise<void> {
		await resolveConcern(id);
		await this.getActiveConcerns();
		await this.getResolvedConcerns();
	}

	@Action
	async promoteConcern(id: string): Promise<void> {
		await promoteConcern(id);
		await this.getActiveConcerns();
		await ProblemsModule.getActiveProblems();
	}
}

export const ConcernsModule = getModule(Concerns);
