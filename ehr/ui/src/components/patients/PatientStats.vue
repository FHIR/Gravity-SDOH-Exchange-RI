<script lang="ts">
import { computed, defineComponent } from "vue";
import { ConcernsModule } from "@/store/modules/concerns";
import { ProblemsModule } from "@/store/modules/problems";
import { Goal, Problem, Task } from "@/types";
import { GoalsModule } from "@/store/modules/goals";
import { TasksModule } from "@/store/modules/tasks";

export default defineComponent({
	name: "PatientStats",
	setup() {
		const activeConcerns = computed<number>(() => ConcernsModule.activeConcerns.length);
		const activeProblems = computed<Problem[]>(() => ProblemsModule.problems.filter(t => t.clinicalStatus === "active"));
		const activeGoals = computed<Goal[]>(() => GoalsModule.goals.filter(t => t.status === "active"));
		const activeActionSteps = computed<Task[]>(() => TasksModule.tasks.filter(t => t.status !== "Completed"));


		return {
			activeConcerns,
			activeProblems,
			activeGoals,
			activeActionSteps
		};
	}
});
</script>

<template>
	<el-card class="patient-stats">
		<div class="info-block">
			<div class="info-item">
				<div class="value">
					{{ activeConcerns }}
				</div>
				<div class="label">
					Health Concerns
				</div>
			</div>
			<div class="info-item">
				<div class="value">
					{{ activeProblems.length }}
				</div>
				<div class="label">
					Active Problems
				</div>
			</div>
			<div class="info-item">
				<div class="value">
					{{ activeGoals.length }}
				</div>
				<div class="label">
					Active Goals
				</div>
			</div>
			<div class="info-item">
				<div class="value">
					{{ activeActionSteps.length }}
				</div>
				<div class="label">
					Active Action Steps
				</div>
			</div>
		</div>
	</el-card>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.patient-stats {
	height: 100%;
}

.info-block {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 100%;
}

.info-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	margin-right: 25px;

	&:last-child {
		margin-right: 0;
	}

	.value {
		font-size: 64px;
		color: $global-primary-color;
		font-weight: $global-font-weight-medium;
	}

	.label {
		font-size: $global-font-size;
		text-align: center;
	}
}
</style>
