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
		const tasks = ref<TaskWithState[]>([]);
		const activeRequests = computed<Task[]>(() => TasksModule.activeRequests);
		const inactiveRequests = computed<Task[]>(() => TasksModule.inactiveRequests);
		const showLoader = computed<boolean>(() => TasksModule.isLoading);

		onMounted( async () => {
			try {
				await TasksModule.getTasks();
			} finally {
				props.requestType === "active" ?
					tasks.value = activeRequests.value.map((task: Task) => ({ task, isNew: false })) :
					tasks.value = inactiveRequests.value.map((task: Task) => ({ task, isNew: false }));
			}
		});

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
