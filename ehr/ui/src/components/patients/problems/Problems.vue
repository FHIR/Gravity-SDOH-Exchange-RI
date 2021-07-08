<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
import ProblemsTable from "@/components/patients/problems/ProblemsTable.vue";
import { Problem } from "@/types";
import { ProblemsModule } from "@/store/modules/problems";
import NewProblemDialog from "@/components/patients/problems/NewProblemDialog.vue";
import NoActiveItems from "@/components/patients/NoActiveItems.vue";
import NoItems from "@/components/patients/NoItems.vue";

export type TableData = {
	id: string,
	name: string,
	basedOn: string,
	startDate?: string,
	closedDate?: string,
	goals: number,
	actionSteps: number,
	status: string,
	codeISD: string,
	codeSNOMED: string,
	category: string
};

export default defineComponent({
	name: "Problems",
	components: {
		NoItems,
		NoActiveItems,
		NewProblemDialog,
		ProblemsTable
	},
	emits: ["trigger-add-goal"],
	setup() {
		const problems = computed<Problem[]>(() => ProblemsModule.problems);
		const tableData = computed<TableData[]>(() =>
			problems.value.map((problem: Problem) => ({
				id: problem.id,
				name: problem.name,
				basedOn: problem.basedOn,
				startDate: problem.onsetPeriod.start,
				closedDate: problem.onsetPeriod.end,
				goals: problem.goals,
				actionSteps: problem.actionSteps,
				status: problem.clinicalStatus,
				codeISD: problem.codeISD,
				codeSNOMED: problem.codeSNOMED,
				category: problem.category
			}))
		);

		const activeProblems = computed<TableData[]>(() => tableData.value.filter(t => t.status === "active"));
		const closedProblems = computed<TableData[]>(() => tableData.value.filter(t => t.status === "resolved"));

		const newProblemsDialogVisible = ref<boolean>(false);

		const isLoading = ref<boolean>(false);

		onMounted(async () => {
			isLoading.value = true;
			try {
				await ProblemsModule.getProblems();
			} finally {
				isLoading.value = false;
			}
		});

		return {
			isLoading,
			tableData,
			activeProblems,
			closedProblems,
			newProblemsDialogVisible
		};
	}
});
</script>

<template>
	<div
		v-loading="isLoading"
		class="problems"
	>
		<ProblemsTable
			v-if="activeProblems.length"
			:data="activeProblems"
			title="Active Problems"
			status="active"
			@add-problem="newProblemsDialogVisible = true"
			@trigger-add-goal="$emit('trigger-add-goal', $event)"
		/>
		<NoActiveItems
			v-else-if="!activeProblems.length && closedProblems.length"
			message="No Active Problems"
			button-label="Add Problem"
			@add-item="newProblemsDialogVisible = true"
		/>
		<ProblemsTable
			v-if="closedProblems.length"
			:data="closedProblems"
			title="Closed Problems"
			status="closed"
		/>
		<NoItems
			v-if="!isLoading && !tableData.length"
			massage="No Problems Yet"
			button-label="Add Problem"
			@add-item="newProblemsDialogVisible = true"
		/>
		<NewProblemDialog
			:visible="newProblemsDialogVisible"
			@close="newProblemsDialogVisible = false"
		/>
	</div>
</template>

