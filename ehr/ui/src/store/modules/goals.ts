import { getGoals } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Goal } from "@/types";

export interface IGoals {
	goals: Goal[]
}

@Module({ dynamic: true, store, name: "goals" })
class Goals extends VuexModule implements IGoals {
	goals: Goal[] = [];

	@Mutation
	setGoals(payload: Goal[]): void {
		this.goals = payload;
	}

	@Action
	async getGoals(): Promise<void> {
		const data = await getGoals();

		this.setGoals(data);
	}
}

export const GoalsModule = getModule(Goals);
