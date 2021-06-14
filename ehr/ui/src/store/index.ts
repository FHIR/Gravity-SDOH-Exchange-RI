import { createStore } from "vuex";
import { config } from "vuex-module-decorators";
// Set rawError to true by default on all @Action decorators
config.rawError = true;
import { IContext } from "@/store/modules/context";
import { ITasks } from "@/store/modules/tasks";
import { IGoals } from "@/store/modules/goals";

export interface IRootState {
	context: IContext,
	tasks: ITasks,
	goals: IGoals
}

export default createStore<IRootState>({
	strict: process.env.NODE_ENV !== "production"
});
