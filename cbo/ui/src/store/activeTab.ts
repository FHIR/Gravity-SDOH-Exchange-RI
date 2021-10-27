import { VuexModule, Module, getModule, Mutation } from "vuex-module-decorators";
import store from "@/store";
import { ACTIVE_REQUESTS_TAB } from "@/utils/constants";

export interface ITab {
	activeTab: string
}

@Module({ dynamic: true, store, name: "activeTab" })
class ActiveTab extends VuexModule implements ITab {
	activeTab: string = ACTIVE_REQUESTS_TAB;

	@Mutation
	setActiveTab(tabName: string) {
		this.activeTab = tabName;
	}
}

export const ActiveTabModule = getModule(ActiveTab);
