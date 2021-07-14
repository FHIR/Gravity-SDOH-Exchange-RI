<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/goals/Goals.vue";
import ActionButton from "@/components/patients/ActionButton.vue";
import GoalDialog from "@/components/patients/goals/GoalDialog.vue";
import { GoalStatus } from "@/types";

export type GoalAction = "edit" | "mark-as-completed" | "view" | "remove";

export default defineComponent({
	name: "GoalsTable",
	components: {
		ActionButton,
		GoalDialog
	},
	props: {
		data: {
			type: Array as PropType<TableData[]>,
			required: true
		},
		status: {
			type: String as PropType<GoalStatus>,
			default: "active"
		}
	},
	emits: ["add-goal"],
	setup(props) {
		const title = ref<string>(props.status === "active" ? "Active Goals" : "Completed Goals");

		const dialogVisible = ref<boolean>(false);
		const dialogOpenPhase = ref<GoalAction>();
		const activeGoal = ref<TableData>();

		const onGoalActionClick = (action: GoalAction, row: TableData) => {
			dialogOpenPhase.value = action;
			activeGoal.value = row;
			dialogVisible.value = true;
		};

		return {
			title,
			dialogVisible,
			activeGoal,
			dialogOpenPhase,
			onGoalActionClick
		};
	}
});
</script>

<template>
	<div class="table-wrapper">
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
				@click="$emit('add-goal')"
			>
				Add Goal
			</el-button>
		</div>
		<el-table :data="data">
			<el-table-column label="Goal">
				<template #default="scope">
					<el-button
						type="text"
						@click="onGoalActionClick(status === 'active' ? 'edit' : 'view', scope.row)"
					>
						{{ scope.row.name }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column label="Problem(s)">
				<template #default="scope">
					<el-button type="text">
						{{ scope.row.problems.length > 1 ? `${scope.row.problems.length} Problems` : scope.row.problems.length === 1 ? scope.row.problems[0].display : "0" }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column
				label="Added By"
				prop="addedBy"
			>
				<template #default="scope">
					{{ scope.row.addedBy.display }}
				</template>
			</el-table-column>

			<el-table-column label="Start Date">
				<template #default="scope">
					{{ $filters.formatDateTime(scope.row.startDate) }}
				</template>
			</el-table-column>

			<el-table-column label="Targets">
				<template #default="scope">
					<el-button type="text">
						{{ scope.row.targets ? scope.row.targets : "0" }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column
				v-if="status === 'active'"
				label="Actions"
				width="350"
			>
				<template #default="scope">
					<ActionButton
						icon-class="mark-as-completed"
						label="Mark as Completed"
						@click="onGoalActionClick('mark-as-completed', scope.row)"
					/>
					<ActionButton
						icon-class="remove-goal"
						label="Remove"
						@click="onGoalActionClick('remove', scope.row)"
					/>
					<ActionButton
						icon-class="add-target"
						label="Add Target"
					/>
				</template>
			</el-table-column>

			<el-table-column
				v-if="status === 'completed'"
				label="End Date"
			>
				<template #default="scope">
					{{ $filters.formatDateTime(scope.row.endDate) }}
				</template>
			</el-table-column>
		</el-table>
	</div>

	<GoalDialog
		:visible="dialogVisible"
		:goal="activeGoal"
		:open-phase="dialogOpenPhase"
		@close="dialogVisible = false"
	/>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.title {
	margin-top: 10px;
	display: flex;
	justify-content: space-between;

	h3 {
		font-weight: $global-font-weight-medium;
		margin: 0 0 0 20px;
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
