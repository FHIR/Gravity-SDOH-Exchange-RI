import { VuexModule, Module, getModule, Action, Mutation } from "vuex-module-decorators";
import store from "@/store";
import { newProblemPayload, Problem } from "@/types";
import { createProblem, getActiveProblems, getClosedProblems, closeProblem } from "@/api";

export interface IProblems {
	activeProblems: Problem[]
	closedProblems: Problem[]
}

@Module({ dynamic: true, store, name: "problems" })
class Problems extends VuexModule implements IProblems {
	activeProblems: Problem[] = []
	closedProblems: Problem[] = []

	@Mutation
	setActiveProblems(payload: Problem[]): void {
		this.activeProblems = payload;
	}

	@Mutation
	addActiveProblem(payload: Problem) {
		this.activeProblems = [ payload, ...this.activeProblems ];
	}

	@Mutation
	setClosedProblems(payload: Problem[]): void {
		this.closedProblems = payload;
	}

	@Action
	async getActiveProblems(): Promise<void> {
		const data = await getActiveProblems();

		this.setActiveProblems(data);
	}

	@Action
	async getClosedProblems(): Promise<void> {
		const data = await getClosedProblems();

		this.setClosedProblems(data);
	}

	@Action
	async createProblem(payload: newProblemPayload): Promise<void> {
		const data = await createProblem(payload);

		this.addActiveProblem(data);
	}

	@Action
	async closeProblem(payload: string): Promise<void> {
		await closeProblem(payload);
		await this.getActiveProblems();
		await this.getClosedProblems();
	}
}

export const ProblemsModule = getModule(Problems);
