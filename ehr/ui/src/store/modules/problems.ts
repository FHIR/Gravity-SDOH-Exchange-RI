import { VuexModule, Module, getModule, Action, Mutation } from "vuex-module-decorators";
import store from "@/store";
import { Problem } from "@/types";
import { getProblems } from "@/api";

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
}

export const ProblemsModule = getModule(Problems);
