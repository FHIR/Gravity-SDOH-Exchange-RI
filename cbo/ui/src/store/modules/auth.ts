import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";

export interface IAuth {
	isAuthenticated: boolean
}

const AUTH_KEY = "authenticated";

@Module({ dynamic: true, store, name: "auth" })
class Auth extends VuexModule implements IAuth {
	isAuthenticated: boolean = localStorage.getItem(AUTH_KEY) === "true" || false;

	@Mutation
	setAuthenticated(payload: boolean): void {
		this.isAuthenticated = payload;
	}

	@Action
	login(payload: boolean): void {
		localStorage.setItem(AUTH_KEY, `${payload}`);
		this.setAuthenticated(payload);
	}

	@Action
	logout(): void {
		localStorage.removeItem(AUTH_KEY);
		this.setAuthenticated(false);
	}
}

export const AuthModule = getModule(Auth);
