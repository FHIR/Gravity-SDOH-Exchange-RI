<script lang="ts">
import { computed, defineComponent, PropType, ref, watch } from "vue";
import { Goal, Coding, Comment } from "@/types";
import { GoalsModule } from "@/store/modules/goals";
import GoalsTable from "@/components/patients/goals/GoalsTable.vue";
import NewGoalDialog from "@/components/patients/goals/NewGoalDialog.vue";
import NoActiveItems from "@/components/patients/NoActiveItems.vue";
import NoItems from "@/components/patients/NoItems.vue";

export const ACHIEVEMENT_STATUSES = [
	{ code: "INPROGRESS", display: "In progress" },
	{ code: "IMPROVING", display: "Improving" },
	{ code: "WORSENING", display: "Worsening" },
	{ code: "NOCHANGE", display: "No Change" },
	{ code: "ACHIEVED", display: "Achieved" },
	{ code: "SUSTAINING", display: "Sustaining" },
	{ code: "NOTACHIEVED", display: "Not Achieved" },
	{ code: "NOPROGRESS", display: "No Progress" },
	{ code: "NOTATTAINABLE", display: "Not Attainable" }
];

export type TableData = {
	name: string,
	problems: string[],
	addedBy: string,
	startDate: string,
	endDate: string,
	targets: string[],
	status: string,
	category: Coding,
	snomedCode: Coding,
	id: string,
	comments: Comment[]
	achievementStatus: string
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
		const activeGoals = computed<Goal[]>(() => GoalsModule.activeGoals);
		const completedGoals = computed<Goal[]>(() => GoalsModule.completedGoals);
		const activeGoalsTableData = computed<TableData[]>(() =>
			activeGoals.value.map((goal: Goal) => ({
				name: goal.name,
				problems: goal.problems,
				addedBy: goal.addedBy,
				startDate: goal.startDate,
				endDate: goal.endDate,
				targets: goal.targets,
				status: goal.status,
				category: goal.category,
				snomedCode: goal.snomedCode,
				id: goal.id,
				comments: goal.comments,
				achievementStatus: goal.achievementStatus
			}))
		);

		const completedGoalsTableData = computed<TableData[]>(() =>
			completedGoals.value.map((goal: Goal) => ({
				name: goal.name,
				problems: goal.problems,
				addedBy: goal.addedBy,
				startDate: goal.startDate,
				endDate: goal.endDate,
				targets: goal.targets,
				status: goal.status,
				category: goal.category,
				snomedCode: goal.snomedCode,
				id: goal.id,
				comments: goal.comments,
				achievementStatus: goal.achievementStatus
			}))
		);

		const newGoalDialogVisible = ref<boolean>(false);

		const handleDialogClose = () => {
			newGoalDialogVisible.value = false;
			emit("stop-add-goal");
		};

		watch(() => props.isActive, async active => {
			if (active) {
				isDataLoading.value = true;
				try {
					await GoalsModule.getActiveGoals();
					await GoalsModule.getCompletedGoals();
					newGoalDialogVisible.value = props.addGoalPhase;
				} finally {
					isDataLoading.value = false;
				}
			}
		}, { immediate: true });

		return {
			activeGoalsTableData,
			completedGoalsTableData,
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
			v-if="activeGoalsTableData.length > 0"
			:data="activeGoalsTableData"
			status="active"
			@add-goal="newGoalDialogVisible = true"
		/>
		<NoActiveItems
			v-else-if="!activeGoalsTableData.length && completedGoalsTableData.length"
			message="No Active Goals"
			button-label="Add Goal"
			@add-item="newGoalDialogVisible = true"
		/>
		<GoalsTable
			v-if="completedGoalsTableData.length > 0"
			:data="completedGoalsTableData"
			status="completed"
		/>
		<NoItems
			v-if="!isDataLoading && !(activeGoalsTableData.length > 0 || completedGoalsTableData.length > 0)"
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
