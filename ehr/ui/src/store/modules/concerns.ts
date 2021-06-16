import { Action, getModule, Module, Mutation, VuexModule } from "vuex-module-decorators";
import { getConcerns } from "@/api";
import store from "@/store";
import { Concern, NewConcernPayload } from "@/types";

export interface IConcerns {
	concerns: Concern[]
}

@Module({ dynamic: true, store, name: "concern" })
class Concerns extends VuexModule implements IConcerns {
	concerns: Concern[] = [];

	@Mutation
	setConcerns(payload: Concern[]): void {
		this.concerns = payload;
	}

	@Mutation
	addConcern(payload: Concern): void {
		this.concerns = [...this.concerns, payload];
	}

	@Action
	async getConcerns(): Promise<void> {
		const data = await getConcerns();

		this.setConcerns(data);
	}

	@Action
	createConcern(payload: NewConcernPayload) {
		this.addConcern(addConcernResponse(payload));
	}
}

// TODO: Delete when BE will be ready
const addConcernResponse = (payload: NewConcernPayload): Concern => ({
	name: payload.name,
	assessmentDate: payload.assessmentDate,
	category: payload.category,
	basedOn: payload.basedOn,
	status: payload.status,
	concernStatus: payload.concernStatus
});

export const ConcernsModule = getModule(Concerns);
