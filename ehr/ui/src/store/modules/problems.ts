import { VuexModule, Module, getModule, Action, Mutation } from "vuex-module-decorators";
import store from "@/store";
import { newProblemPayload, Problem, updateProblemPayload } from "@/types";
import { getProblems, createProblem, updateProblem } from "@/api";

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

	@Mutation
	changeProblem(payload: Problem) {
		this.problems = this.problems.map(item => item.id === payload.id ? payload : item);
	}

	@Action
	async getProblems(): Promise<void> {
		const data = await getProblems();

		this.setProblems(data);
	}

	@Action
	async createProblem(payload: newProblemPayload): Promise<void> {
		await createProblem(payload);
		// todo: check if we get response on create problem. if yes add new problem to list, if not get all problems again
		await getProblems();
	}

	@Action
	async updateProblem(payload: updateProblemPayload): Promise<void> {
		const updatedProblem = await updateProblem(payload);
		this.changeProblem(updatedProblem);
	}
}

export const ProblemsModule = getModule(Problems);
