import { createStore } from "vuex";
import { config } from "vuex-module-decorators";
// Set rawError to true by default on all @Action decorators
config.rawError = true;
import { IContext } from "@/store/modules/context";
import { ITasks } from "@/store/modules/tasks";

export interface IRootState {
	context: IContext,
	tasks: ITasks
}

export default createStore<IRootState>({
	strict: process.env.NODE_ENV !== "production"
});
