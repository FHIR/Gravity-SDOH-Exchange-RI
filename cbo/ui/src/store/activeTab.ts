import { VuexModule, Module, getModule, Mutation } from "vuex-module-decorators";
import store from "@/store";
import { ACTIVE_REQUESTS_TAB } from "@/utils/constants";

export interface ITab {
	activeTab: string,
	activeTaskLength: number,
	inactiveTaskLength: number,
}

@Module({ dynamic: true, store, name: "activeTab" })
class ActiveTab extends VuexModule implements ITab {
	activeTab: string = ACTIVE_REQUESTS_TAB;
	activeTaskLength: number = 0;
	inactiveTaskLength: number = 0;

	@Mutation
	setActiveTab(tabName: string) {
		this.activeTab = tabName;
	}

	@Mutation
	setActiveTasksLength(length: number) {
		this.activeTaskLength = length;
	}

	@Mutation
	setInactiveTasksLength(length: number) {
		this.inactiveTaskLength = length;
	}
}

export const ActiveTabModule = getModule(ActiveTab);
