import { VuexModule, Module, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Problem } from "@/types";

export interface IProblems {
	problems: Problem[]
}

@Module({ dynamic: true, store, name: "problems" })
class Problems extends VuexModule implements IProblems {
	problems: Problem[] = [{
		id: "SDOHCC-Condition-HungerVitalSign-Example-1",
		name: "Hunger Vital Signs",
		basedOn: "Hunger Vital Signs assessment",
		onsetPeriod: {
			start: "2019-08-18T12:31:35.123Z"
		},
		goals: 0,
		actionSteps: 0,
		clinicalStatus: "active",
		code: "Lack of Adequate Food & Safe Drinking Water (Z59.49)",
		category: "test"
	},
	{
		id: "SDOHCC-Condition-HungerVitalSign-Example-2",
		name: "Hunger Vital Signs",
		basedOn: "Hunger Vital Signs assessment",
		onsetPeriod: {
			start: "2019-08-18T12:31:35.123Z"
		},
		goals: 0,
		actionSteps: 0,
		clinicalStatus: "resolved",
		code: "Insuficient Food Supply (706875005)",
		category: "test"
	}];
}

export const ProblemsModule = getModule(Problems);
