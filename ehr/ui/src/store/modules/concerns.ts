import { Action, getModule, Module, Mutation, VuexModule } from "vuex-module-decorators";
import { getConcerns } from "@/api";
import store from "@/store";
import { Concern } from "@/types";

export interface IConcerns {
	concerns: Concern[]
}

@Module({ dynamic: true, store, name: "concern" })
class Concerns extends VuexModule implements IConcerns {
	concerns: Concern[] = [];

	@Mutation
	setConcerns(payload: any) {
		this.concerns = payload;
	}

	@Mutation
	updateConcerns(payload: any) {
		this.concerns = [...this.concerns, payload];
	}

	@Action
	async getConcerns(): Promise<void> {
		const data = await getConcerns();

		this.setConcerns(data);
	}

	@Action
	createConcern(payload: any) {
		this.updateConcerns(payload);
	}
}

export const ConcernsModule = getModule(Concerns);
