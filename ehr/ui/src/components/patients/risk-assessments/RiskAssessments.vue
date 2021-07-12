<script lang="ts">
import { computed, defineComponent, ref, watch } from "vue";
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
	setup(props, { emit }) {
		const activeGroup = ref<"pastAssessments" | "plannedAssessments">("pastAssessments");
		const isRequestLoading = computed(() => AssessmentsModule.assessmentsLoading);
		const assessments = computed<Assessment[]>(() => AssessmentsModule.assessments);

		const viewingAssessment = ref<Assessment | undefined>(undefined);

		AssessmentsModule.loadPastAssessments();

		const handleDialogClose = () => {
			viewingAssessment.value = undefined;
			emit("stop-open-assessment");
		};

		watch(() => props.isActive, () => {
			if (props.isActive && props.openAssessmentPhase) {
				viewingAssessment.value = findAssessmentById(props.assessmentToOpen, assessments.value);
			}
		});

		const findAssessmentById = (id: string, assessments: Assessment[]) => {
			let assessment: Assessment | undefined;

			assessment = assessments.find(assessment => assessment.id === id);

			if (!assessment) {
				assessment = assessments.reduce((result: Assessment | undefined, assessment: Assessment) => {
					result = assessment.previous?.find(assessment => assessment.id === id);
					return result;
				}, undefined);
			}

			return assessment;
		};

		return {
			activeGroup,
			assessments,
			isRequestLoading,
			viewingAssessment,
			handleDialogClose
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
				@stop-open-assessment="$emit('stop-open-assessment')"
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
			@close="handleDialogClose"
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
