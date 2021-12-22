<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
import { Task, TaskWithState } from "@/types";
import TaskTable from "@/components/TaskTable.vue";
import Filters from "@/components/Filters.vue";
import TableCard from "@/components/TableCard.vue";
import TaskEditDialog from "@/components/TaskEditDialog.vue";
import TaskResourcesDialog from "@/components/TaskResourcesDialog.vue";
import { TasksModule } from "@/store/modules/tasks";
import { showUpdates } from "@/utils";

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
		const tasks = computed<TaskWithState[]>(() => props.requestType === "active" ? TasksModule.activeRequests: TasksModule.inactiveRequests);
		const showLoader = computed<boolean>(() => TasksModule.isLoading);

		onMounted( () => {
			TasksModule.getTasks();
		});

		const taskInEdit = ref<Task | null>(null);

		const editTask = (taskToEdit: TaskWithState) => {
			TasksModule.markTaskAsNotNew(taskToEdit.task.id);
			taskInEdit.value = taskToEdit.task;
		};

		const closeDialog = () => {
			taskInEdit.value = null;
		};

		const taskToViewResources = ref<TaskWithState | null>(null);
		const viewTaskResources = (task: TaskWithState) => taskToViewResources.value = task;

		const search = ref<string>("");
		const handleSearch = (payload: string) => {
			search.value = payload;
		};
		const searchedTasks = computed<TaskWithState[]>(() => {
			const normalizedSearch = search.value.toLowerCase();

			return normalizedSearch ? tasks.value.filter(({ task }) =>
				task.name.toLowerCase().includes(normalizedSearch) ||
				task.requester.display.toLowerCase().includes(normalizedSearch) ||
				task.patient.display.toLowerCase().includes(normalizedSearch)
			) : tasks.value;
		});

		return {
			searchedTasks,
			editTask,
			closeDialog,
			taskInEdit,
			viewTaskResources,
			taskToViewResources,
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
				:task="taskToViewResources"
				@close="taskToViewResources = null"
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
