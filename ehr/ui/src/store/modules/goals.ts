import { createGoal, getActiveGoals, getCompletedGoals, updateGoal, removeGoal, markGoalAsCompleted } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Goal, GoalAsCompletedPayload, NewGoalPayload, UpdateGoalPayload } from "@/types";

export interface IGoals {
	activeGoals: Goal[]
	completedGoals: Goal[]
}

@Module({ dynamic: true, store, name: "goals" })
class Goals extends VuexModule implements IGoals {
	activeGoals: Goal[] = [];
	completedGoals: Goal[] = [];

	@Mutation
	setActiveGoals(payload: Goal[]): void {
		this.activeGoals = payload;
	}

	@Mutation
	setCompletedGoals(payload: Goal[]): void {
		this.completedGoals = payload;
	}


	@Mutation
	changeGoal(payload: Goal): void {
		this.activeGoals = this.activeGoals.map(goal => goal.id === payload.id ? payload : goal);
	}

	@Action
	async getActiveGoals(): Promise<void> {
		const data = await getActiveGoals();

		this.setActiveGoals(data);
	}

	@Action
	async getCompletedGoals(): Promise<void> {
		const data = await getCompletedGoals();

		this.setCompletedGoals(data);
	}

	@Action
	async updateGoal(payload: UpdateGoalPayload): Promise<void> {
		const updatedTask = await updateGoal(payload);

		this.changeGoal(updatedTask);
	}

	@Action
	async createGoal(payload: NewGoalPayload): Promise<void> {
		await createGoal(payload);
		await this.getActiveGoals();
	}

	@Action
	async removeGoal(id: string): Promise<void> {
		await removeGoal(id);
		await this.getActiveGoals();
	}

	@Action
	async markGoalAsCompleted(payload: GoalAsCompletedPayload): Promise<void> {
		await markGoalAsCompleted(payload);
		await this.getCompletedGoals();
		await this.getActiveGoals();
	}
}

export const GoalsModule = getModule(Goals);
