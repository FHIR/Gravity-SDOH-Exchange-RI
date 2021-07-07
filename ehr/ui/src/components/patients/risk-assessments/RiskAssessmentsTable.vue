<script lang="ts">
import { defineComponent, PropType, ref, watch } from "vue";
import { TableData } from "@/components/patients/risk-assessments/RiskAssessments.vue";
import EditAssessmentDialog from "@/components/patients/risk-assessments/EditAssessmentDialog.vue";

export default defineComponent({
	name: "RiskAssessmentsTable",
	components: {
		EditAssessmentDialog
	},
	props: {
		data: {
			type: Array as PropType<TableData[]>,
			required: true
		},
		assessmentId: {
			type: String,
			required: true
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
		const editAssessmentDialogVisible = ref<boolean>(false);
		const editAssessment = ref<TableData>();
		const onRequestClick = (row: TableData) => {
			editAssessmentDialogVisible.value = true;
			editAssessment.value = row;
		};

		const handleDialogClose = () => {
			editAssessmentDialogVisible.value = false;
			emit("stop-open-assessment");
		};

		watch(() => props.isActive, () => {
			if (props.isActive) {
				editAssessment.value = props.data.find(assessment => assessment.id === props.assessmentId);
				editAssessmentDialogVisible.value = props.openAssessmentPhase;
			}
		});

		return {
			editAssessmentDialogVisible,
			onRequestClick,
			editAssessment,
			handleDialogClose
		};
	}
});
</script>

<template>
	<div>
		<div
			class="table-wrapper"
		>
			<el-table :data="data">
				<el-table-column
					label="Planned Assessment Name"
				>
					<template #default="scope">
						<el-button
							type="text"
							@click="onRequestClick(scope.row)"
						>
							{{ scope.row.name }}
						</el-button>
					</template>
				</el-table-column>
				<el-table-column
					label="Assessment Date"
				>
					<template #default="scope">
						<div class="status-cell">
							<div class="info">
								<span>{{ $filters.formatDateTime(scope.row.createdAt) }}</span>
							</div>
						</div>
					</template>
				</el-table-column>
				<el-table-column
					label="Identified Health Concerns"
				>
					<template #default="scope">
						<a :class="{underline: scope.row.concerns.length}">
							{{ scope.row.concerns }}
						</a>
					</template>
				</el-table-column>
				<el-table-column />
			</el-table>
		</div>

		<EditAssessmentDialog
			:visible="editAssessmentDialogVisible"
			:assessment="editAssessment"
			@close="handleDialogClose"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.title {
	margin: 10px 20px 0;

	h3 {
		font-weight: $global-font-weight-medium;
		margin: 0;
	}
}

.table-wrapper {
	background-color: $global-background;
	border-radius: 5px;
	border: 1px solid $global-base-border-color;
	padding: 10px 20px;
	min-height: 130px;
}

.underline {
	text-decoration: underline;
}
</style>
