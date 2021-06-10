<script lang="ts">
import { defineComponent, PropType, ref } from "vue";
import { TableData } from "@/components/patients/problems/Problems.vue";
import ActionButton from "@/components/patients/ActionButton.vue";
import ProblemDialog from "@/components/patients/problems/ProblemDialog.vue";

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
		}
	},
	setup() {
		const problemsDialogVisible = ref<boolean>(false);
		const activeProblem = ref<TableData>();

		const onRequestClick = (row: TableData) => {
			problemsDialogVisible.value = true;
			activeProblem.value = row;
		};

		return {
			problemsDialogVisible,
			activeProblem,
			onRequestClick
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
		</div>
		<el-table :data="data">
			<el-table-column
				label="Problem"
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
				label="Based on"
			>
				<template #default="scope">
					<span>{{ scope.row.basedOn }}</span>
				</template>
			</el-table-column>
			<el-table-column
				label="Start Date"
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
				label="Actions"
				width="350"
			>
				<ActionButton
					icon-class="add-goal"
					label="Add Goal"
				/>
				<ActionButton
					icon-class="add-action-step"
					label="Add Action Step"
				/>
				<ActionButton
					icon-class="mark-as-closed"
					label="Mark As Closed"
				/>
			</el-table-column>
		</el-table>

		<ProblemDialog
			:visible="problemsDialogVisible"
			:problem="activeProblem"
			@close="problemsDialogVisible = false"
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

	+ .table-wrapper {
		margin-top: 30px;
	}
}
</style>
