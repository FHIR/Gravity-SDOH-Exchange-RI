<script lang="ts">
import { computed, defineComponent, ref } from "vue";
import RiskAssessmentsTable from "@/components/patients/risk-assessments/RiskAssessmentsTable.vue";
import { Assessment } from "@/types";
import { AssessmentsModule } from "@/store/modules/assessments";
import NoItems from "@/components/patients/NoItems.vue";

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
		NoItems,
		RiskAssessmentsTable
	},
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
			/>
			<NoItems
				v-if="!isRequestLoading && !pastAssessments.length"
				message="No Past Assessments Yet"
				button-label="Import Assessment"
			/>
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
</style>
