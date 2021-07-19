import { VuexModule, Module, getModule, Action, Mutation } from "vuex-module-decorators";
import store from "@/store";
import { ActiveResources } from "@/types";
import { getActiveResources } from "@/api";

export interface IResources {
	activeResources: ActiveResources
}

@Module({ dynamic: true, store, name: "activeResources" })
class Resources extends VuexModule implements IResources {
	activeResources: ActiveResources = {
		activeConcernsCount: 0,
		activeGoalsCount: 0,
		activeInterventionsCount: 0,
		activeProblemsCount: 0
	};

	@Mutation
	setActiveResources(data: ActiveResources) {
		this.activeResources = data;
	}

	@Action
	async loadActiveResources() {
		const data = await getActiveResources();
		this.setActiveResources(data);
	}
}

export const ActiveResourcesModule = getModule(Resources);
