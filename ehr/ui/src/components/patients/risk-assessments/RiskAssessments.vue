<script lang="ts">
import { computed, defineComponent, ref } from "vue";
import RiskAssessmentsTable from "@/components/patients/risk-assessments/RiskAssessmentsTable.vue";
import EditAssessmentDialog from "@/components/patients/risk-assessments/EditAssessmentDialog.vue";
import { Assessment } from "@/types";
import { AssessmentsModule } from "@/store/modules/assessments";
import NoItems from "@/components/patients/NoItems.vue";


export default defineComponent({
	components: {
		NoItems,
		RiskAssessmentsTable,
		EditAssessmentDialog
	},
	setup() {
		const activeGroup = ref<"pastAssessments" | "plannedAssessments">("pastAssessments");
		const isRequestLoading = computed(() => AssessmentsModule.assessmentsLoading);
		const assessments = computed<Assessment[]>(() => AssessmentsModule.assessments);

		const viewingAssessment = ref<Assessment | undefined>(undefined);

		AssessmentsModule.loadPastAssessments();

		return {
			activeGroup,
			assessments,
			isRequestLoading,
			viewingAssessment
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
				v-if="activeGroup === 'pastAssessments'"
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
				v-if="assessments.length"
				:data="assessments"
				@title-click="viewingAssessment = $event"
			/>
			<NoItems
				v-if="!isRequestLoading && !assessments.length"
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

		<EditAssessmentDialog
			:visible="viewingAssessment !== undefined"
			:assessment="viewingAssessment"
			@close="viewingAssessment = undefined"
		/>
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
