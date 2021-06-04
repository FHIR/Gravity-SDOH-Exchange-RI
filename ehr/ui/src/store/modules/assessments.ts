import { VuexModule, Module, getModule } from "vuex-module-decorators";
import store from "@/store";
import { Assessment } from "@/types";

export interface IAssessments {
	assessments: Assessment[]
}

@Module({ dynamic: true, store, name: "assessment" })
class Assessments extends VuexModule implements IAssessments {
	assessments: Assessment[] = [{ id: "123123",  name: "Hunger Vital Signs", createdAt: "2021-05-18T14:15:08", concerns: "Food Insecurity", status: "Past", actions: "send to patient", questions: ["asgdagdj", "How do you do?"] }, { id: "123123",  name: "Hunger Vital Signs", createdAt: "2021-05-18T14:15:08", concerns: "Food Insecurity", status: "Past", actions: "send to patient", questions: ["asgdagdj", "How do you do?"] }];
}

export const AssessmentsModule = getModule(Assessments);
