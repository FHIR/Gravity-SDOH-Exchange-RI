<script lang="ts">
import { defineComponent, ref } from "vue";
import HealthConcerns from "@/components/patients/health-concerns/HealthConcerns.vue";
import ActionSteps from "@/components/patients/action-steps/ActionSteps.vue";
import RiskAssessments from "@/components/patients/risk-assessments/RiskAssessments.vue";
import Problems from "@/components/patients/problems/Problems.vue";
import Goals from "@/components/patients/goals/Goals.vue";
import Consents from "@/components/patients/consents/Consents.vue";
import PatientTasks from "@/components/patients/patient-tasks/PatientTasks.vue";

export default defineComponent({
	name: "Tabs",
	components: {
		HealthConcerns,
		Problems,
		ActionSteps,
		RiskAssessments,
		Goals,
		Consents,
		PatientTasks
	},
	setup() {
		const activeTab = ref<string>("healthConcerns");
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

		const addActionPhase = ref<boolean>(false);
		const newActionProblems = ref<string[]>([]);

		const handleAddActionFromProblem = (problemId: string) => {
			activeTab.value = "actionSteps";
			addActionPhase.value = true;
			newActionProblems.value = [problemId];
		};

		const resetAddActionPhase =  () => {
			addActionPhase.value = false;
			newActionProblems.value = [];
		};

		return {
			activeTab,
			addGoalPhase,
			newGoalProblems,
			handleAddGoalFromProblem,
			resetAddGoalPhase,
			addActionPhase,
			newActionProblems,
			handleAddActionFromProblem,
			resetAddActionPhase,
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
			:lazy="true"
		>
			<HealthConcerns
				:is-active="activeTab === 'healthConcerns'"
				@trigger-open-assessment="openAssessment"
			/>
		</el-tab-pane>
		<el-tab-pane
			label="Problems"
			name="problems"
			:lazy="true"
		>
			<Problems
				:is-active="activeTab === 'problems'"
				@trigger-add-goal="handleAddGoalFromProblem"
				@trigger-open-assessment="openAssessment"
				@trigger-add-action-step="handleAddActionFromProblem"
			/>
		</el-tab-pane>
		<el-tab-pane
			label="Goals"
			name="goals"
			:lazy="true"
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
			:lazy="true"
		>
			<ActionSteps
				:add-action-phase="addActionPhase"
				:new-action-problems="newActionProblems"
				:is-active="activeTab === 'actionSteps'"
				@stop-add-action="resetAddActionPhase"
			/>
		</el-tab-pane>
		<el-tab-pane
			label="Patient Tasks"
			name="patientTasks"
			:lazy="true"
		>
			<PatientTasks
				@trigger-open-assessment="openAssessment"
			/>
		</el-tab-pane>
		<el-tab-pane
			label="Social Risk Assessments"
			name="socialRiskAssessments"
			:lazy="true"
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
			:lazy="true"
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

		> div {
			min-height: 130px;
		}
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
