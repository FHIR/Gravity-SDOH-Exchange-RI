<script lang="ts">
import { computed, defineComponent, ref } from "vue";
import RiskAssessmentsTable from "@/components/patients/risk-assessments/RiskAssessmentsTable.vue";
import { Assessment } from "@/types";
import { AssessmentsModule } from "@/store/modules/assessments";

export type TableData = {
	name: string,
	createdAt: string,
	concerns: string
	id: string,
	questions: string[],
	actions: string
	status: string
}

export default defineComponent({
	name: "RiskAssessments",
	components: {
		RiskAssessmentsTable
	},
	props: {
		assessmentToOpen: {
			type: String,
			default: ""
		},
		isActive: {
			type: Boolean,
			default: false
		},
		openAssessmentPhase: {
			type: Boolean,
			default: false
		}
	},
	emits: ["stop-open-assessment"],
	setup() {
		const activeGroup = ref<string>("pastAssessments");
		const isRequestLoading = ref<boolean>(false);
		const assessments = computed<Assessment[]>(() => AssessmentsModule.assessments);
		const tableData = computed<TableData[]>(() =>
			assessments.value.map((assessment: Assessment) => ({
				name: assessment.name,
				createdAt: assessment.createdAt,
				concerns: assessment.concerns,
				id: assessment.id,
				questions: assessment.questions,
				actions: assessment.actions,
				status: assessment.status
			}))
		);

		const pastAssessments = computed<TableData[]>(() => tableData.value.filter(t => t.status === "Past"));

		return {
			activeGroup,
			pastAssessments,
			isRequestLoading
		};
	}
});
</script>

<template>
	<div class="risk-assessments">
		<div class="risk-assessments-switcher">
			<el-radio-group
				v-model="activeGroup"
				size="mini"
			>
				<el-radio-button label="pastAssessments">
					Past Assessments
				</el-radio-button>
				<el-radio-button label="plannedAssessments">
					Planned Assessments
				</el-radio-button>
			</el-radio-group>
			<el-button
				v-if="activeGroup === 'pastAssessments' && pastAssessments.length"
				plain
				round
				type="primary"
				size="mini"
			>
				Import Assessment
			</el-button>
		</div>
		<div
			v-if="activeGroup === 'pastAssessments'"
			v-loading="isRequestLoading"
			class="planned-assessments"
		>
			<RiskAssessmentsTable
				v-if="pastAssessments.length"
				:data="pastAssessments"
				:assessment-id="assessmentToOpen"
				:is-active="isActive"
				:open-assessment-phase="openAssessmentPhase"
				@stop-open-assessment="$emit('stop-open-assessment')"
			/>
			<div
				v-if="!isRequestLoading && !pastAssessments.length"
				class="no-request-data"
			>
				<h2>No Past Assessments Yet</h2>
				<el-button
					plain
					round
					type="primary"
					size="mini"
				>
					Import Assessment
				</el-button>
			</div>
		</div>
		<div
			v-if="activeGroup === 'plannedAssessments'"
			class="plannedAssessments"
		>
			Planned Assessments
		</div>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.risk-assessments-switcher {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30px;
}

.planned-assessments {
	min-height: 130px;
}

.no-request-data {
	height: 240px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: flex-start;
	background-color: $global-background;

	h2 {
		color: $whisper;
		font-size: $global-xxxlarge-font-size;
		font-weight: $global-font-weight-normal;
		margin-bottom: 50px;
	}
}
</style>
