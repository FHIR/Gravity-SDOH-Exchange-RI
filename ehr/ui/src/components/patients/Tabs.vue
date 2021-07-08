<script lang="ts">
import { defineComponent, ref } from "vue";
import HealthConcerns from "@/components/patients/health-concerns/HealthConcerns.vue";
import ActionSteps from "@/components/patients/action-steps/ActionSteps.vue";
import RiskAssessments from "@/components/patients/risk-assessments/RiskAssessments.vue";
import Problems from "@/components/patients/problems/Problems.vue";
import Goals from "@/components/patients/goals/Goals.vue";
import Consents from "@/components/patients/consents/Consents.vue";

export default defineComponent({
	name: "Tabs",
	components: {
		HealthConcerns,
		Problems,
		ActionSteps,
		RiskAssessments,
		Goals,
		Consents
	},
	setup() {
		const activeTab = ref<string>("actionSteps");
		const addGoalPhase = ref<boolean>(false);
		const newGoalProblems = ref<string[]>([]);
		const assessmentToOpenId = ref<string>("");
		const openAssessmentPhase = ref<boolean>(false);

		const openAssessment = (id: string) => {
			assessmentToOpenId.value = id;
			openAssessmentPhase.value = true;
			activeTab.value = "socialRiskAssessments";
		};

		const handleAddGoalFromProblem = (problemId: string) => {
			activeTab.value = "goals";
			addGoalPhase.value = true;
			newGoalProblems.value = [problemId];
		};

		const resetAddGoalPhase = () => {
			addGoalPhase.value = false;
			newGoalProblems.value = [];
		};

		return {
			activeTab,
			addGoalPhase,
			newGoalProblems,
			handleAddGoalFromProblem,
			resetAddGoalPhase,
			openAssessment,
			assessmentToOpenId,
			openAssessmentPhase
		};
	}
});
</script>

<template>
	<el-tabs v-model="activeTab">
		<el-tab-pane
			label="Health Concerns"
			name="healthConcerns"
		>
			<HealthConcerns
				@trigger-open-assessment="openAssessment"
			/>
		</el-tab-pane>
		<el-tab-pane
			label="Problems"
			name="problems"
		>
			<Problems
				@trigger-add-goal="handleAddGoalFromProblem"
				@trigger-open-assessment="openAssessment"
			/>
		</el-tab-pane>
		<el-tab-pane
			label="Goals"
			name="goals"
		>
			<Goals
				:add-goal-phase="addGoalPhase"
				:new-goals-problems="newGoalProblems"
				:is-active="activeTab === 'goals'"
				@stop-add-goal="resetAddGoalPhase"
			/>
		</el-tab-pane>
		<el-tab-pane
			label="Action Steps"
			name="actionSteps"
		>
			<ActionSteps />
		</el-tab-pane>
		<el-tab-pane
			label="Social Risk Assessments"
			name="socialRiskAssessments"
		>
			<RiskAssessments
				:open-assessment-phase="openAssessmentPhase"
				:assessment-to-open="assessmentToOpenId"
				:is-active="activeTab === 'socialRiskAssessments'"
				@stop-open-assessment="openAssessmentPhase = false"
			/>
		</el-tab-pane>
		<el-tab-pane
			label="Consents"
			name="consents"
		>
			<Consents />
		</el-tab-pane>
	</el-tabs>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.el-tabs {
	width: 100%;
	background-color: $global-background;
	border-radius: 5px;
	box-shadow: $global-box-shadow;

	::v-deep(.el-tabs__header) {
		margin: 0;
	}

	::v-deep(.el-tabs__nav-wrap) {
		border-bottom: 1px solid $global-base-border-color;
		padding: 0 25px;

		&::after {
			height: 0;
		}
	}

	::v-deep(.el-tabs__active-bar) {
		box-sizing: content-box;
		padding: 0 20px;
		margin-left: -20px;
	}

	::v-deep(.el-tab-pane) {
		padding: 20px;
	}

	::v-deep(.el-tabs__item) {
		font-weight: $global-font-weight-normal;
		padding: 20px 35px;
		height: 60px;
		line-height: 20px;

		&:hover,
		&.is-active {
			color: $global-text-color;
		}

		&.is-top:nth-child(2) {
			padding-left: 35px;
		}
	}
}
</style>
