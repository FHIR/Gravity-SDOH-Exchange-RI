<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/problems/Problems.vue";
import ActionButton from "@/components/patients/ActionButton.vue";
import ProblemDialog from "@/components/patients/problems/ProblemDialog.vue";

export type ProblemDialogPhase = "view" | "mark-as-closed" | "add-action-step";
export type ProblemActionType = "view" | "add-goal" | "add-action-step" | "mark-as-closed";
export const ACTIVE_TASK_STATUSES = ["ACCEPTED", "DRAFT", "IN PROGRESS", "ON HOLD", "READY", "RECEIVED", "REQUESTED"];

export default defineComponent({
	name: "ProblemsTable",
	components: { ActionButton, ProblemDialog },
	props: {
		data: {
			type: Array as PropType<TableData[]>,
			required: true
		},
		title: {
			type: String,
			required: true
		},
		status: {
			type: String,
			default: "active"
		}
	},
	emits: ["add-problem", "trigger-open-assessment", "trigger-add-goal", "trigger-add-action-step"],
	setup(props, { emit }) {
		const problemsDialogVisible = ref<boolean>(false);
		const activeProblem = ref<TableData | null>(null);
		const problemsDialogOpenPhase = ref<ProblemActionType>("view");

		const handleActionClick = (action: ProblemActionType, problem: TableData) => {
			if (action === "mark-as-closed" || action === "view" || action === "add-action-step") {
				problemsDialogOpenPhase.value = action;
				problemsDialogVisible.value = true;
				activeProblem.value = problem;
			}

			if (action === "add-goal") {
				// trigger event to open goal tab and show 'add goal' dialog
				emit("trigger-add-goal", problem.id);
			}
		};

		const handleOpenAssessment = (id: string) => {
			problemsDialogVisible.value = false;
			emit("trigger-open-assessment", id);
		};

		const canProblemBeClosed = (problem: TableData): boolean => problem.goals.some(item => item.status === "ACTIVE") || problem.tasks.some(item => ACTIVE_TASK_STATUSES.includes(item.status));

		return {
			problemsDialogVisible,
			activeProblem,
			problemsDialogOpenPhase,
			handleActionClick,
			handleOpenAssessment,
			canProblemBeClosed
		};
	}
});
</script>

<template>
	<div
		class="table-wrapper"
	>
		<div class="title">
			<h3>
				{{ title }}
			</h3>
			<el-button
				v-if="status === 'active'"
				plain
				round
				type="primary"
				size="mini"
				@click="$emit('add-problem')"
			>
				Add Problem
			</el-button>
		</div>
		<el-table :data="data">
			<el-table-column
				label="Problem"
			>
				<template #default="scope">
					<el-button
						type="text"
						@click="handleActionClick('view', scope.row)"
					>
						{{ scope.row.name }}
					</el-button>
				</template>
			</el-table-column>
			<el-table-column
				label="Based on"
			>
				<template #default="scope">
					{{ scope.row.basedOn.display ? scope.row.basedOn.display : scope.row.basedOn }}
					<span
						v-if="scope.row.basedOn.id"
						class="icon-link"
						@click="$emit('trigger-open-assessment', scope.row.basedOn.id)"
					>
					</span>
				</template>
			</el-table-column>
			<el-table-column
				label="Start Date"
			>
				<template #default="scope">
					{{ scope.row.startDate ? $filters.formatDateTime(scope.row.startDate) : "N/A" }}
				</template>
			</el-table-column>
			<el-table-column
				label="Goal(s)"
			>
				<template #default="scope">
					<span>{{ scope.row.goals.length }}</span>
				</template>
			</el-table-column>
			<el-table-column
				label="Action Steps"
			>
				<template #default="scope">
					<span>{{ scope.row.tasks.length }}</span>
				</template>
			</el-table-column>
			<el-table-column
				v-if="status === 'active'"
				label="Actions"
				width="350"
			>
				<template #default="scope">
					<ActionButton
						icon-class="add-goal"
						label="Add Goal"
						@click="handleActionClick('add-goal', scope.row)"
					/>
					<ActionButton
						icon-class="add-action-step"
						label="Add Action Step"
						@click="handleActionClick('add-action-step', scope.row)"
					/>
					<ActionButton
						icon-class="mark-as-closed"
						label="Mark as Closed"
						:disabled="canProblemBeClosed(scope.row)"
						@click="handleActionClick('mark-as-closed', scope.row)"
					/>
				</template>
			</el-table-column>
			<el-table-column
				v-if="status === 'closed'"
				label="Closed Date"
				width="350"
			>
				<template #default="scope">
					<span>{{ $filters.formatDateTime(scope.row?.closedDate) }}</span>
				</template>
			</el-table-column>
		</el-table>

		<ProblemDialog
			:visible="problemsDialogVisible"
			:problem="activeProblem"
			:open-phase="problemsDialogOpenPhase"
			:status="status"
			@close="problemsDialogVisible = false"
			@trigger-add-goal="$emit('trigger-add-goal', $event);"
			@trigger-add-action-step="$emit('trigger-add-action-step', $event)"
			@trigger-open-assessment="handleOpenAssessment"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

.title {
	margin: 10px 20px 0;
	display: flex;
	justify-content: space-between;

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

	+ .table-wrapper {
		margin-top: 30px;
	}
}

.icon-link {
	position: relative;
	left: 7px;
	cursor: pointer;

	@include icon("~@/assets/images/link.svg", 14px, 14px);
}
</style>
