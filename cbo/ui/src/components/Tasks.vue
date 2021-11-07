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
		const tasksWithState = ref<TaskWithState[]>([]);
		const tasks = computed<Task[]>(() => props.requestType === "active" ? activeRequests.value: inactiveRequests.value);
		const activeRequests = computed<Task[]>(() => TasksModule.activeRequests);
		const inactiveRequests = computed<Task[]>(() => TasksModule.inactiveRequests);
		const showLoader = computed<boolean>(() => TasksModule.isLoading);
		const addStateToTasks = () => tasksWithState.value = tasks.value.map(task => ({ task, isNew: false }));

		onMounted( async () => {
			try {
				await TasksModule.getTasks(true);
				addStateToTasks();

				watch(() => TasksModule.tasks, (newTasks, prevTasks) => {
					if (newTasks.length && prevTasks?.length) {
						showUpdates(newTasks, prevTasks);
						updateTasks(newTasks);
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
			() =>
				true
			,
			5000
		);

		const isTaskNew = (task: Task) => {
			const existingTask = tasksWithState.value.find(ts => ts.task.id === task.id);
			return existingTask === undefined || existingTask.isNew;
		};

		const updateTasks = (newList: Task[]) => {
			tasksWithState.value = newList.map(task => ({
				task,
				isNew: isTaskNew(task)
			}));
		};

		const taskInEdit = ref<Task | null>(null);

		const editTask = (taskToEdit: TaskWithState) => {
			markTaskAsNotNew(taskToEdit.task.id);
			taskInEdit.value = taskToEdit.task;
		};

		const markTaskAsNotNew = (taskId: string) => {
			tasksWithState.value = tasksWithState.value.map(taskState => taskState.task.id === taskId ? { ...taskState, isNew: false } : taskState);
		};

		const closeDialog = () => {
			taskInEdit.value = null;
		};

		const taskIdToViewResources = ref<string | null>(null);
		const viewTaskResources = (taskId: string) => {
			taskIdToViewResources.value = taskId;
		};

		const search = ref<string>("");
		const handleSearch = (payload: string) => {
			search.value = payload;
		};
		const searchedTasks = computed<TaskWithState[]>(() => {
			const normalizedSearch = search.value.toLowerCase();

			return normalizedSearch ? tasksWithState.value.filter(({ task }) =>
				task.name.toLowerCase().includes(normalizedSearch) ||
				task.requester.display.toLowerCase().includes(normalizedSearch) ||
				task.patient.display.toLowerCase().includes(normalizedSearch)
			) : tasksWithState.value;
		});

		return {
			searchedTasks,
			editTask,
			closeDialog,
			taskInEdit,
			viewTaskResources,
			taskIdToViewResources,
			showLoader,
			handleSearch
		};
	}
});
</script>

<template>
	<div class="tasks">
		<Filters @search="handleSearch" />
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
				:tasks="searchedTasks"
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
