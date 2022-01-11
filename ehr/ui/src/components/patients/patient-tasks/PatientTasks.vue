<script lang="ts">
import { defineComponent, ref, computed, onMounted, onUnmounted, watch, h } from "vue";
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
import TaskStatusIcon from "@/components/patients/TaskStatusIcon.vue";
import { ElNotification } from "element-plus";
import { taskStatusDiff } from "@/components/patients/action-steps/ActionSteps.vue";

export type TableData = {
	id: string,
	name: string,
	type: string | null,
	status: TaskStatus,
	lastModified: string | null,
	code: Coding | null,
	priority: string | null,
	referralTask?: {
		id: string,
		display: string
	} | null,
	assessment?: {
		id: string,
		display: string
	} | null,
	outcomes?: string | null,
	statusReason?: string | null
};

const TYPE_VALUE_MAP: { [key: string]: string} = {
	MAKE_CONTACT: "Make Contact",
	COMPLETE_SR_QUESTIONNAIRE: "Complete questionnaire regarding social risks",
	SERVICE_FEEDBACK: "Provide feedback on service delivered"
};

export default defineComponent({
	components: {
		NoItems,
		NoActiveItems,
		PatientTasksTable,
		NewPatientTaskDialog
	},
	emits: ["trigger-open-assessment", "trigger-open-action-step"],
	setup() {
		const isTaskLoading = ref<boolean>(false);
		const newPatientTaskDialogVisible = ref<boolean>(false);
		const tasks = computed<PatientTask[]>(() => PatientTasksModule.patientTasks);
		const tableData = computed<TableData[]>(() =>
			tasks.value.map((task: PatientTask) => ({
				id: task.id,
				name: task.name,
				type:  TYPE_VALUE_MAP[task.type],
				status: task.status,
				lastModified: task.lastModified,
				code: task.code,
				referralTask: task.referralTask,
				assessment: task.assessment,
				outcomes: task.outcome,
				statusReason: task.statusReason,
				priority: task.priority,
				assessmentResponse: task.assessmentResponse
			}))
		);
		const activeTasks = computed<TableData[]>(() => tableData.value.filter(t => t.status !== "Completed"));
		const completedTasks = computed<TableData[]>(() => tableData.value.filter(t => t.status === "Completed"));

		onMounted(async () => {
			isTaskLoading.value = true;
			try {
				await PatientTasksModule.getPatientTasks();
				pollData();
			} finally {
				isTaskLoading.value = false;
			}
		});
		const pollId = ref<number>();
		const pollData = async () => {
			await PatientTasksModule.getPatientTasks();
			pollId.value = window.setTimeout(pollData, 5000);
		};
		onUnmounted(() => {
			clearTimeout(pollId.value);
		});

		// todo: check what are the rules for notifications show
		const findDiff = (val: PatientTask[], oldVal: PatientTask[]): taskStatusDiff[] =>
			val.flatMap((task: PatientTask) => {
				const existingTask = oldVal.find(t => t.id === task.id);
				if (!existingTask) {
					return [];
				}

				const oldStatus = existingTask.status;
				const newStatus = task.status;
				if (oldStatus === newStatus) {
					return [];
				}

				return [{
					name: task.name,
					oldStatus,
					newStatus
				}];
			});

		watch(() => tasks.value, (val, oldVal) => {
			const diff = findDiff(val, oldVal);

			diff.forEach(update => {
				const message = h("p", [
					`Patient changed status of task "${update.name}" from `,
					h(TaskStatusIcon, {
						status: update.oldStatus,
						small: true,
						showLabel: true
					}),
					" to ",
					h(TaskStatusIcon,{
						status: update.newStatus,
						small: true,
						showLabel: true
					})
				]);

				ElNotification({
					title: "Notification",
					iconClass: "notification-bell",
					duration: 10000,
					message
				});
			});
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
			@add-task="newPatientTaskDialogVisible = true"
			@trigger-open-assessment="$emit('trigger-open-assessment', $event)"
			@trigger-open-action-step="$emit('trigger-open-action-step', $event)"
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
			@trigger-open-assessment="$emit('trigger-open-assessment', $event)"
			@trigger-open-action-step="$emit('trigger-open-action-step', $event)"
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
