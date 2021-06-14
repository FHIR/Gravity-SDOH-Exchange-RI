import { VuexModule, Module, getModule, Action, Mutation } from "vuex-module-decorators";
import store from "@/store";
import { newProblem, Problem } from "@/types";
import { getProblems, createProblem } from "@/api";

export interface IProblems {
	problems: Problem[]
}

@Module({ dynamic: true, store, name: "problems" })
class Problems extends VuexModule implements IProblems {
	problems: Problem[] = []

	@Mutation
	setProblems(payload: Problem[]): void {
		this.problems = payload;
	}

	@Action
	async getProblems(): Promise<void> {
		const data = await getProblems();

		this.setProblems(data);
	}

	@Action
	async createProblem(payload: newProblem): Promise<void> {
		await createProblem(payload);
		// todo: check if we get response on create problem. if yes add new problem to list, if not get all problems again
		await getProblems();
	}
}

export const ProblemsModule = getModule(Problems);
