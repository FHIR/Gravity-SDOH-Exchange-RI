import { createStore } from "vuex";
import { config } from "vuex-module-decorators";
import { IAuth } from "@/store/modules/auth";
import { IServers } from "@/store/modules/servers";
import { ITasks } from "@/store/modules/tasks";
// Set rawError to true by default on all @Action decorators
config.rawError = true;

export interface IRootState {
	auth: IAuth,
	servers: IServers,
	tasks: ITasks
}

export default createStore<IRootState>({
	strict: process.env.NODE_ENV !== "production"
});
