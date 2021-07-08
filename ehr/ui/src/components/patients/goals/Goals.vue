<script lang="ts">
import { computed, defineComponent, onMounted, PropType, ref, watch } from "vue";
import { Goal, Coding, Comment } from "@/types";
import { GoalsModule } from "@/store/modules/goals";
import GoalsTable from "@/components/patients/goals/GoalsTable.vue";
import NewGoalDialog from "@/components/patients/goals/NewGoalDialog.vue";
import NoActiveItems from "@/components/patients/NoActiveItems.vue";
import NoItems from "@/components/patients/NoItems.vue";

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
		NoItems,
		NoActiveItems,
		GoalsTable
	},
	props: {
		isActive: {
			type: Boolean,
			default: false
		},
		addGoalPhase: {
			type: Boolean,
			default: false
		},
		newGoalsProblems: {
			type: Array as PropType<string[]>,
			default: () => []
		}
	},
	emits: ["stop-add-goal"],
	setup(props, { emit }) {
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

		const handleDialogClose = () => {
			newGoalDialogVisible.value = false;
			emit("stop-add-goal");
		};

		watch(() => props.isActive, () => {
			if (props.isActive) {
				newGoalDialogVisible.value = props.addGoalPhase;
			}
		});

		return {
			activeGoals,
			completedGoals,
			isDataLoading,
			newGoalDialogVisible,
			handleDialogClose
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
		<NoActiveItems
			v-else-if="!activeGoals.length && completedGoals.length"
			message="No Active Goals"
			button-label="Add Goal"
		/>
		<GoalsTable
			v-if="completedGoals.length > 0"
			:data="completedGoals"
			status="completed"
		/>
		<NoItems
			v-if="!isDataLoading && !(activeGoals.length > 0 || completedGoals.length > 0)"
			message="No Goals Yet"
			button-label="Add Goal"
			@add-item="newGoalDialogVisible = true"
		/>
		<NewGoalDialog
			:new-goals-problems="newGoalsProblems"
			:visible="newGoalDialogVisible"
			@close="handleDialogClose"
		/>
	</div>
</template>
