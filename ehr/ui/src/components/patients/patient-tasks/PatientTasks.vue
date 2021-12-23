<script lang="ts">
import { defineComponent, ref, computed, onMounted, onUnmounted } from "vue";
import NoItems from "@/components/patients/NoItems.vue";
import NoActiveItems from "@/components/patients/NoActiveItems.vue";
import PatientTasksTable from "@/components/patients/patient-tasks/PatientTasksTable.vue";
import NewPatientTaskDialog from "@/components/patients/patient-tasks/NewPatientTaskDialog.vue";
import { PatientTasksModule } from "@/store/modules/patientTasks";
import {
	Coding,
	PatientTask,
	TaskStatus
} from "@/types";

export type TableData = {
	id: string,
	name: string,
	type: string | null,
	status: TaskStatus,
	lastModified: string | null,
	code: Coding | null,
	referral: {
		id: string,
		display: string
	} | null,
	assessment: {
		id: string,
		display: string
	} | null,
	outcomes: string | null,
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
				id: task.id,
				name: task.name,
				type: task.type,
				status: task.status,
				lastModified: task.lastModified,
				code: task.code,
				referral: task.referralTask,
				assessment: task.assessment,
				outcomes: task.outcome,
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
