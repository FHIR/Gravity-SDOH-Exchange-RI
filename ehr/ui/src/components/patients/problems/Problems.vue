<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
import ProblemsTable from "@/components/patients/problems/ProblemsTable.vue";
import { Coding, Problem } from "@/types";
import { ProblemsModule } from "@/store/modules/problems";
import NewProblemDialog from "@/components/patients/problems/NewProblemDialog.vue";
import NoActiveItems from "@/components/patients/NoActiveItems.vue";
import NoItems from "@/components/patients/NoItems.vue";

export type TableData = {
	id: string,
	name: string,
	basedOn: string | {
		display: string,
		id: string,
	},
	assessmentDate?: string,
	startDate?: string,
	resolutionDate?: string,
	goals: number,
	actionSteps: number,
	category: Coding,
	icdCode: Coding,
	snomedCode: Coding
};

export default defineComponent({
	name: "Problems",
	components: {
		NoItems,
		NoActiveItems,
		NewProblemDialog,
		ProblemsTable
	},
	emits: ["trigger-open-assessment", "trigger-add-goal"],
	setup() {
		const activeProblems = computed<Problem[]>(() => ProblemsModule.activeProblems);
		const closedProblems = computed<Problem[]>(() => ProblemsModule.closedProblems);
		const activeProblemsTableData = computed<TableData[]>(() =>
			activeProblems.value.map((problem: Problem) => ({
				id: problem.id,
				name: problem.name,
				basedOn: problem.basedOn,
				assessmentDate: problem.assessmentDate || "",
				startDate: problem.startDate || "",
				goals: 0,
				actionSteps: 0,
				icdCode: problem.icdCode,
				snomedCode: problem.snomedCode,
				category: problem.category
			}))
		);
		const closedProblemsTableData = computed<TableData[]>(() =>
			closedProblems.value.map((problem: Problem) => ({
				id: problem.id,
				name: problem.name,
				basedOn: problem.basedOn,
				assessmentDate: problem.assessmentDate || "",
				startDate: problem.startDate || "",
				resolutionDate: problem.resolutionDate || "",
				goals: 0,
				actionSteps: 0,
				icdCode: problem.icdCode,
				snomedCode: problem.snomedCode,
				category: problem.category
			}))
		);


		const newProblemsDialogVisible = ref<boolean>(false);

		const isLoading = ref<boolean>(false);

		onMounted(async () => {
			isLoading.value = true;
			try {
				await ProblemsModule.getActiveProblems();
				await ProblemsModule.getClosedProblems();
			} finally {
				isLoading.value = false;
			}
		});

		return {
			isLoading,
			activeProblemsTableData,
			closedProblemsTableData,
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
			v-if="activeProblemsTableData.length"
			:data="activeProblemsTableData"
			title="Active Problems"
			status="active"
			@add-problem="newProblemsDialogVisible = true"
			@trigger-add-goal="$emit('trigger-add-goal', $event)"
			@trigger-open-assessment="$emit('trigger-open-assessment', $event)"
		/>
		<NoActiveItems
			v-else-if="!activeProblemsTableData.length && closedProblemsTableData.length"
			message="No Active Problems"
			button-label="Add Problem"
			@add-item="newProblemsDialogVisible = true"
		/>
		<ProblemsTable
			v-if="closedProblemsTableData.length"
			:data="closedProblemsTableData"
			title="Closed Problems"
			status="closed"
			@trigger-open-assessment="$emit('trigger-open-assessment', $event)"
		/>
		<NoItems
			v-if="!isLoading && !activeProblemsTableData.length && !closedProblemsTableData.length"
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

