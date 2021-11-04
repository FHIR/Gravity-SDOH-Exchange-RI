<script lang="ts">
import { computed, defineComponent, onMounted, ref, watch, h } from "vue";
import { Task, TaskWithState } from "@/types";
import TaskTable from "@/components/TaskTable.vue";
import Filters from "@/components/Filters.vue";
import TableCard from "@/components/TableCard.vue";
import TaskEditDialog from "@/components/TaskEditDialog.vue";
import TaskResourcesDialog from "@/components/TaskResourcesDialog.vue";
import { TasksModule } from "@/store/modules/tasks";
import { ElNotification } from "element-plus";
import TaskStatusDisplay from "@/components/TaskStatusDisplay.vue";


const poll = <T>(
	makeRequest: () => Promise<T>,
	proceed: (t: T) => boolean,
	ms: number
) => {
	const next = () => {
		setTimeout(async () => {
			try {
				const resp = await makeRequest();
				if (proceed(resp)) {
					next();
				}
			} catch {
				next();
			}
		}, ms);
	};
	next();
};

export default defineComponent({
	components: {
		TableCard,
		Filters,
		TaskTable,
		TaskEditDialog,
		TaskResourcesDialog
	},
	props: {
		requestType: {
			type: String,
			required: true
		}
	},
	setup(props) {
		const tasks = computed<TaskWithState[]>(() => props.requestType === "active" ? activeRequests.value: inactiveRequests.value);
		const activeRequests = computed<TaskWithState[]>(() => TasksModule.activeRequests);
		const inactiveRequests = computed<TaskWithState[]>(() => TasksModule.inactiveRequests);
		const showLoader = computed<boolean>(() => TasksModule.isLoading);

		onMounted( async () => {
			try {
				await TasksModule.getTasks();
				watch(() => TasksModule.tasks, (newTasks, prevTasks) => {
					if (newTasks && prevTasks) {
						showUpdates(newTasks, prevTasks);
					}
				}, { immediate: true });
			} catch {}
		});

		const showUpdates = (newList: Task[], oldList: Task[]) => {
			findUpdates(newList, oldList).forEach(update => {
				const message = h("p", [
					`CP changed status of task "${update.name}" from `,
					//todo: for some reason ts pops up error about incorrect import
					// @ts-ignore
					h(TaskStatusDisplay, {
						status: update.oldStatus,
						small: true
					}),
					" to ",
					// @ts-ignore
					h(TaskStatusDisplay,{
						status: update.newStatus,
						small: true
					})
				]);

				ElNotification({
					title: "Notification",
					iconClass: "notification-bell",
					duration: 10000,
					message
				});
			});
		};

		const findUpdates = (newList: Task[], oldList: Task[]): { name: string, oldStatus: string, newStatus: string }[] =>
			newList.flatMap(task => {
				const existingTask = oldList.find(ts => ts.id === task.id);
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

		poll(
			TasksModule.getTasks,
			newResp => {
				// showUpdates(newResp);
				// updateTasks(newResp);
				return true;
			},
			5000
		);

		const taskInEdit = ref<Task | null>(null);

		const editTask = (taskToEdit: TaskWithState) => {
			// markTaskAsNotNew(taskToEdit.task.id);
			taskInEdit.value = taskToEdit.task;
		};

		const closeDialog = () => {
			taskInEdit.value = null;
		};

		const taskIdToViewResources = ref<string | null>(null);
		const viewTaskResources = (taskId: string) => {
			taskIdToViewResources.value = taskId;
		};

		return {
			tasks,
			editTask,
			closeDialog,
			taskInEdit,
			viewTaskResources,
			taskIdToViewResources,
			showLoader
		};
	}
});
</script>

<template>
	<div class="tasks">
		<Filters />
		<TableCard>
			<TaskEditDialog
				:task="taskInEdit"
				@close="closeDialog"
			/>

			<TaskResourcesDialog
				:task-id="taskIdToViewResources"
				@close="taskIdToViewResources = null"
			/>

			<TaskTable
				:tasks="tasks"
				:loading="showLoader"
				@task-name-click="editTask"
				@view-resources="viewTaskResources"
			/>
		</TableCard>
	</div>
</template>

<style lang="scss" scoped>
@import "~@/assets/scss/abstracts/variables";

.tasks {
	height: 100%;
	display: flex;
	flex-direction: column;
	background-color: $alice-blue;

	.filters {
		margin-bottom: 30px;
	}

	.table-card {
		flex-grow: 1;
	}
}
</style>
