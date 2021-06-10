<script lang="ts">
import { computed, defineComponent } from "vue";
import ProblemsTable from "@/components/patients/problems/ProblemsTable.vue";
import { Problem } from "@/types";
import { ProblemsModule } from "@/store/modules/problems";

export type TableData = {
	id: string,
	name: string,
	basedOn: string,
	startDate?: string,
	closedDate?: string,
	goals: number,
	actionSteps: number,
	status: string,
	code: string,
	category: string
};

export default defineComponent({
	name: "Problems",
	components: {
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
				code: problem.code,
				category: problem.category
			}))
		);

		const activeProblems = computed<TableData[]>(() => tableData.value.filter(t => t.status === "active"));
		const closedProblems = computed<TableData[]>(() => tableData.value.filter(t => t.status === "resolved"));


		return {
			tableData,
			activeProblems,
			closedProblems
		};
	}
});
</script>

<template>
	<div class="problems">
		<ProblemsTable
			v-if="activeProblems.length"
			:data="activeProblems"
			title="Active Problems"
			type="active problems"
		/>
		<ProblemsTable
			v-if="closedProblems.length"
			:data="closedProblems"
			title="Closed Problems"
			type="closed problems"
		/>
		<div
			v-if="!tableData.length"
			class="no-data"
		>
			<h2>No Problems Yet</h2>
			<el-button
				plain
				round
				type="primary"
				size="mini"
			>
				Add Problem
			</el-button>
		</div>
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
		color: $whisper;
		font-size: $global-xxxlarge-font-size;
		font-weight: $global-font-weight-normal;
		margin-bottom: 50px;
	}
}
</style>
