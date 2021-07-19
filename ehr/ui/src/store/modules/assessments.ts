import { VuexModule, Module, getModule, Action, Mutation } from "vuex-module-decorators";
import store from "@/store";
import { Assessment } from "@/types";
import { getPastAssessments } from "@/api";

export interface IAssessments {
	assessments: Assessment[]
}

@Module({ dynamic: true, store, name: "assessment" })
class Assessments extends VuexModule implements IAssessments {
	assessments: Assessment[] = [];
	assessmentsLoading: boolean = false;

	@Mutation
	setAssessments(data: Assessment[]) {
		this.assessments = data;
	}

	@Mutation
	setAssessmentsLoading(data: boolean) {
		this.assessmentsLoading = data;
	}

	@Action
	async loadPastAssessments() {
		this.setAssessmentsLoading(true);
		try {
			this.setAssessments(await getPastAssessments());
		} finally {
			this.setAssessmentsLoading(false);
		}
	}
}

export const AssessmentsModule = getModule(Assessments);
