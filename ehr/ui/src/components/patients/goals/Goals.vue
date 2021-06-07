<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
import { Goal } from "@/types";
import { GoalsModule } from "@/store/modules/goals";
import GoalsTable from "@/components/patients/goals/GoalsTable.vue";

export type TableData = {
	name: string,
	problems: string[],
	addedBy: string,
	startDate: string,
	endDate: string,
	targets: string[],
	status: string
};

export default defineComponent({
	name: "Goals",
	components: {
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
				status: goal.status
			}))
		);
		const activeGoals = computed<TableData[]>(() => tableData.value.filter(goal => goal.status === "active"));
		const completedGoals = computed<TableData[]>(() => tableData.value.filter(goal => goal.status === "completed"));
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
			isDataLoading
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
		/>
		<GoalsTable
			v-if="completedGoals.length > 0"
			:data="completedGoals"
			status="completed"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.table-wrapper:not(:first-child) {
	margin-top: 30px;
}
</style>
