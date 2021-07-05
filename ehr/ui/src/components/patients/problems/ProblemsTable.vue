<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/problems/Problems.vue";
import ActionButton from "@/components/patients/ActionButton.vue";
import ProblemDialog from "@/components/patients/problems/ProblemDialog.vue";

export type ProblemDialogPhase = "view" | "mark-as-closed";
export type ProblemActionType = "view" | "add-goal" | "add-action-step" | "mark-as-closed";

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
	emits: ["add-problem"],
	setup() {
		const problemsDialogVisible = ref<boolean>(false);
		const activeProblem = ref<TableData | null>(null);
		const problemsDialogOpenPhase = ref<ProblemActionType>("view");

		const handleActionClick = (action: ProblemActionType, problem: TableData) => {
			if (action === "mark-as-closed" || action === "view") {
				problemsDialogOpenPhase.value = action;
				problemsDialogVisible.value = true;
				activeProblem.value = problem;
			}
			if (action === "add-goal") {
				// todo: handle add goal behavior
				// emit(action, problem);
				// go to goal tab and show add goal dialog
			}
		};

		return {
			problemsDialogVisible,
			activeProblem,
			problemsDialogOpenPhase,
			handleActionClick
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
					<span>{{ scope.row.basedOn }}</span>
				</template>
			</el-table-column>
			<el-table-column
				label="Creation Date"
			>
				<template #default="scope">
					<span>{{ $filters.formatDateTime(scope.row.startDate) }}</span>
				</template>
			</el-table-column>
			<el-table-column
				label="Goal(s)"
			>
				<template #default="scope">
					<span>{{ scope.row.goals }}</span>
				</template>
			</el-table-column>
			<el-table-column
				label="Action Steps"
			>
				<template #default="scope">
					<span>{{ scope.row.actionSteps }}</span>
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
					/>
					<ActionButton
						icon-class="mark-as-closed"
						label="Mark as Closed"
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
			@close="problemsDialogVisible = false"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

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
</style>
