<script lang="ts">
import { computed, defineComponent, onMounted, ref } from "vue";
import { Task, TaskWithState } from "@/types";
import TaskTable from "@/components/TaskTable.vue";
import Filters from "@/components/Filters.vue";
import TableCard from "@/components/TableCard.vue";
import TaskEditDialog from "@/components/TaskEditDialog.vue";
import TaskResourcesDialog from "@/components/TaskResourcesDialog.vue";
import { TasksModule } from "@/store/modules/tasks";

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
		const showLoader = ref<Boolean>(false);

		onMounted( async () => {
			try {
				showLoader.value = true;
				await TasksModule.getTasks();
			} finally {
				showLoader.value = false;
			}
		});

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
				@close-dialog="closeDialog"
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
