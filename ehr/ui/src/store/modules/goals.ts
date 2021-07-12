import { createGoal, getGoals, updateGoal } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Goal, NewGoalPayload, UpdateGoalPayload } from "@/types";

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

	@Mutation
	changeGoal(payload: Goal): void {
		this.goals = this.goals.map(goal => goal.id === payload.id ? payload : goal);
	}

	@Action
	async getGoals(): Promise<void> {
		const data = await getGoals();

		this.setGoals(data);
	}

	@Action
	async updateGoal(payload: UpdateGoalPayload): Promise<void> {
		const updatedTask = await updateGoal(payload);

		this.changeGoal(updatedTask);
	}

	@Action
	async createGoal(payload: NewGoalPayload): Promise<void> {
		await createGoal(payload);
		await this.getGoals();
	}
}

export const GoalsModule = getModule(Goals);
