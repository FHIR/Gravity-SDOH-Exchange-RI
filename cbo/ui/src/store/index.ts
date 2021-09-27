import { createStore } from "vuex";
import { config } from "vuex-module-decorators";
import { IAuth } from "@/store/modules/auth";
// Set rawError to true by default on all @Action decorators
config.rawError = true;

export interface IRootState {
	auth: IAuth
}

export default createStore<IRootState>({
	strict: process.env.NODE_ENV !== "production"
});
