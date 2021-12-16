<script lang="ts">
import { defineComponent, ref, computed, onMounted, onUnmounted } from "vue";
import NoItems from "@/components/patients/NoItems.vue";
import NoActiveItems from "@/components/patients/NoActiveItems.vue";
import PatientTasksTable from "@/components/patients/patient-tasks/PatientTasksTable.vue";
import NewPatientTaskDialog from "@/components/patients/patient-tasks/NewPatientTaskDialog.vue";
import { PatientTasksModule } from "@/store/modules/patientTasks";
import {
	Coding,
	Comment,
	Occurrence,
	PatientTask, Procedure,
	ServiceRequestCondition,
	ServiceRequestGoal,
	TaskStatus
} from "@/types";

export type TableData = {
	name: string,
	status: TaskStatus,
	category: Coding,
	problems: ServiceRequestCondition[],
	goals: ServiceRequestGoal[],
	performer: string | null | undefined,
	consent: string
	outcomes: string | null,
	comments: Comment[],
	lastModified: string | null,
	request: Coding,
	priority: string | null,
	occurrence: Occurrence,
	procedures: Procedure[],
	id: string,
	statusReason: string | null
};


export default defineComponent({
	components: {
		NoItems,
		NoActiveItems,
		PatientTasksTable,
		NewPatientTaskDialog
	},
	setup() {
		const isTaskLoading = ref<boolean>(false);
		const newPatientTaskDialogVisible = ref<boolean>(false);
		const tasks = computed<PatientTask[]>(() => PatientTasksModule.tasks);
		const tableData = computed<TableData[]>(() =>
			tasks.value.map((task: PatientTask) => ({
				name: task.name,
				status: task.status,
				category: task.serviceRequest.category,
				problems: task.serviceRequest.conditions,
				goals: task.serviceRequest.goals,
				performer: task.organization?.display,
				consent: task.serviceRequest.consent.display,
				outcomes: task.outcome,
				comments: task.comments,
				lastModified: task.lastModified,
				request: task.serviceRequest.code,
				priority: task.priority,
				occurrence: task.serviceRequest.occurrence,
				procedures: task.procedures,
				id: task.id,
				statusReason: task.statusReason
			}))
		);
		const activeTasks = computed<TableData[]>(() => tableData.value.filter(t => t.status !== "Completed"));
		const completedTasks = computed<TableData[]>(() => tableData.value.filter(t => t.status === "Completed"));

		onMounted(async () => {
			isTaskLoading.value = true;
			try {
				await PatientTasksTable.getTasks();
				pollData();
			} finally {
				isTaskLoading.value = false;
			}
		});
		const pollId = ref<number>();
		const pollData = async () => {
			await PatientTasksTable.getTasks();
			pollId.value = window.setTimeout(pollData, 5000);
		};
		onUnmounted(() => {
			clearTimeout(pollId.value);
		});

		const handleDialogClose = () => {
			newPatientTaskDialogVisible.value = false;
		};

		return {
			isTaskLoading,
			activeTasks,
			completedTasks,
			newPatientTaskDialogVisible,
			handleDialogClose
		};
	}
});
</script>

<template>
	<div
		v-loading="isTaskLoading"
		class="patient-tasks"
	>
		<PatientTasksTable
			v-if="activeTasks.length"
			:data="activeTasks"
			status="active"
		/>
		<NoActiveItems
			v-else-if="!activeTasks.length && completedTasks.length"
			message="No Active Patient Tasks"
			button-label="Add Patient Task"
			@add-item="newPatientTaskDialogVisible = true"
		/>
		<PatientTasksTable
			v-if="completedTasks.length"
			:data="completedTasks"
			status="completed"
		/>
		<NoItems
			v-if="!isTaskLoading && !(completedTasks.length || activeTasks.length)"
			message="No Patient Tasks Yet"
			button-label="Add Patient Task"
			@add-item="newPatientTaskDialogVisible = true"
		/>

		<NewPatientTaskDialog
			:visible="newPatientTaskDialogVisible"
			@close="handleDialogClose"
		/>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

</style>
