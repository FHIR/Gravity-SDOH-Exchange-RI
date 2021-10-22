import { getContext } from "@/api";
import { VuexModule, Module, Action, Mutation, getModule } from "vuex-module-decorators";
import store from "@/store";
import { User } from "@/types";

export interface IContext {
	user: User | null
}

@Module({ dynamic: true, store, name: "context" })
class Context extends VuexModule implements IContext {
	user: User | null = null;

	@Mutation
	setUser(payload: User): void {
		this.user = payload;
	}

	@Action
	async getContext(): Promise<void> {
		const user: User = await getContext();

		this.setUser(user);
	}
}

export const ContextModule = getModule(Context);
