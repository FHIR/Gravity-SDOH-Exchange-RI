<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
import ProblemsTable from "@/components/patients/problems/ProblemsTable.vue";
import { Problem } from "@/types";
import { ProblemsModule } from "@/store/modules/problems";
import NewProblemDialog from "@/components/patients/problems/NewProblemDialog.vue";
import NoActiveItems from "@/components/patients/NoActiveItems.vue";

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
		NoActiveItems,
		NewProblemDialog,
		ProblemsTable
	},
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
		/>
		<NoActiveItems
			v-else-if="!activeProblems.length && closedProblems.length"
			items-name="Problems"
			@add-item="newProblemsDialogVisible = true"
		/>
		<ProblemsTable
			v-if="closedProblems.length"
			:data="closedProblems"
			title="Closed Problems"
			status="closed"
		/>
		<div
			v-if="!isLoading && !tableData.length"
			class="no-data"
		>
			<h2>No Problems Yet</h2>
			<el-button
				plain
				round
				type="primary"
				size="mini"
				@click="newProblemsDialogVisible = true"
			>
				Add Problem
			</el-button>
		</div>
		<NewProblemDialog
			:visible="newProblemsDialogVisible"
			@close="newProblemsDialogVisible = false"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.no-data {
	height: 240px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: flex-start;
	background-color: $global-background;

	h2 {
		color: $global-muted-color;
		font-size: $global-xxxlarge-font-size;
		font-weight: $global-font-weight-normal;
		margin-bottom: 50px;
	}
}
</style>
