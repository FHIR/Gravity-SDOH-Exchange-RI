<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/goals/Goals.vue";
import ActionButton from "@/components/patients/ActionButton.vue";
import GoalDialog from "@/components/patients/goals/GoalDialog.vue";

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
			type: String,
			default: "active"
		}
	},
	setup(props) {
		const title = ref<string>(props.status === "active" ? "Active Goals" : "Completed Goals");

		const goalDialogVisible = ref<boolean>(false);
		const activeGoal = ref<TableData>();

		const onGoalClick = (row: TableData) => {
			goalDialogVisible.value = true;
			activeGoal.value = row;
		};

		return {
			title,
			goalDialogVisible,
			activeGoal,
			onGoalClick
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
			>
				Add Goal
			</el-button>
		</div>
		<el-table :data="data">
			<el-table-column label="Goal">
				<template #default="scope">
					<el-button
						type="text"
						@click="onGoalClick(scope.row)"
					>
						{{ scope.row.name }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column label="Problem(s)">
				<template #default="scope">
					<el-button type="text">
						{{ scope.row.problems }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column
				label="Added By"
				prop="addedBy"
			/>

			<el-table-column label="Start Date">
				<template #default="scope">
					{{ $filters.formatDateTime(scope.row.startDate) }}
				</template>
			</el-table-column>

			<el-table-column label="Targets">
				<template #default="scope">
					<el-button type="text">
						{{ scope.row.targets.length }}
					</el-button>
				</template>
			</el-table-column>

			<el-table-column
				v-if="status === 'active'"
				label="Actions"
				width="350"
			>
				<ActionButton
					icon-class="mark-as-completed"
					label="Mark as Completed"
				/>
				<ActionButton
					icon-class="remove-goal"
					label="Remove"
				/>
				<ActionButton
					icon-class="add-target"
					label="Add Target"
				/>
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
		:visible="goalDialogVisible"
		:goal="activeGoal"
		@close="goalDialogVisible = false"
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
}
</style>
