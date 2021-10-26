<script lang="ts">
import { defineComponent, ref } from "vue";
import { Task, TaskWithState } from "@/types";
import TaskTable from "@/components/TaskTable.vue";
import { getTasks } from "@/api";
import Filters from "@/components/Filters.vue";
import TableCard from "@/components/TableCard.vue";
import TaskEditDialog from "@/components/TaskEditDialog.vue";
import TaskResourcesDialog from "@/components/TaskResourcesDialog.vue";
import { ActiveTabModule } from "@/store/activeTab";

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
		const tasks = ref<TaskWithState[]>([]);
		const activeRequests = ref<Task[]>([]);
		const inactiveRequests = ref<Task[]>([]);
		const showLoader = ref<Boolean>(false);

		showLoader.value = true;
		getTasks().then((resp: Task[]) => {
			activeRequests.value = resp.filter((task: Task) => task.status !== "Completed" && task.status !== "Cancelled");
			inactiveRequests.value = resp.filter((task: Task) => task.status === "Completed" || task.status === "Cancelled");

			ActiveTabModule.setActiveTasksLength(activeRequests.value.length);
			ActiveTabModule.setInactiveTasksLength(inactiveRequests.value.length);

			props.requestType === "active" ?
				tasks.value = activeRequests.value.map((task: Task) => ({ task, isNew: false })) :
				tasks.value = inactiveRequests.value.map((task: Task) => ({ task, isNew: false }));
		}).finally(() => showLoader.value = false);

		const taskInEdit = ref<Task | null>(null);

		const editTask = (taskToEdit: TaskWithState) => {
			// markTaskAsNotNew(taskToEdit.task.id);
			taskInEdit.value = taskToEdit.task;
		};

		const updateTaskFromDialog = (task: Task) => {
			tasks.value = tasks.value.map(taskState => taskState.task.id === task.id ? { ...taskState, task } : taskState);
			closeDialog();
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
			updateTaskFromDialog,
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
				@task-updated="updateTaskFromDialog"
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
