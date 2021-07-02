<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
import { Goal, Coding, Comment } from "@/types";
import { GoalsModule } from "@/store/modules/goals";
import GoalsTable from "@/components/patients/goals/GoalsTable.vue";
import NewGoalDialog from "@/components/patients/goals/NewGoalDialog.vue";

export type TableData = {
	name: string,
	problems: string[],
	addedBy: string,
	startDate: string,
	endDate: string,
	targets: string[],
	status: string,
	category: Coding,
	code: Coding,
	id: string,
	comments: Comment[]
};

export default defineComponent({
	name: "Goals",
	components: {
		NewGoalDialog,
		GoalsTable
	},
	setup() {
		const isDataLoading = ref<boolean>(false);
		const goals = computed<Goal[]>(() => GoalsModule.goals);
		const tableData = computed<TableData[]>(() =>
			goals.value.map((goal: Goal) => ({
				name: goal.name,
				problems: goal.problems,
				addedBy: goal.addedBy,
				startDate: goal.startDate,
				endDate: goal.endDate,
				targets: goal.targets,
				status: goal.status,
				category: goal.category,
				code: goal.code,
				id: goal.id,
				comments: goal.comments
			}))
		);
		const activeGoals = computed<TableData[]>(() => tableData.value.filter(goal => goal.status === "active"));
		const completedGoals = computed<TableData[]>(() => tableData.value.filter(goal => goal.status === "completed"));
		const newGoalDialogVisible = ref<boolean>(false);

		onMounted(async () => {
			isDataLoading.value = true;
			try {
				await GoalsModule.getGoals();
			} finally {
				isDataLoading.value = false;
			}
		});

		return {
			activeGoals,
			completedGoals,
			isDataLoading,
			newGoalDialogVisible
		};
	}
});
</script>

<template>
	<div
		v-loading="isDataLoading"
		class="goals"
	>
		<GoalsTable
			v-if="activeGoals.length > 0"
			:data="activeGoals"
			status="active"
			@add-goal="newGoalDialogVisible = true"
		/>
		<GoalsTable
			v-if="completedGoals.length > 0"
			:data="completedGoals"
			status="completed"
		/>
		<div
			v-if="!isDataLoading && !(activeGoals.length > 0 || completedGoals.length > 0)"
			class="no-data"
		>
			<h2>No Goals Yet</h2>
			<el-button
				plain
				round
				type="primary"
				size="mini"
				@click="newGoalDialogVisible = true"
			>
				Add Goal
			</el-button>
		</div>
		<NewGoalDialog
			:visible="newGoalDialogVisible"
			@close="newGoalDialogVisible = false"
		/>
	</div>
</template>
