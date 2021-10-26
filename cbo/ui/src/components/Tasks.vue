<script lang="ts">
import { defineComponent, ref } from "vue";
import { Task, TaskWithState } from "@/types";
import TaskTable from "@/components/TaskTable.vue";
import { getTasks } from "@/api";
import Filters from "@/components/Filters.vue";
import TableCard from "@/components/TableCard.vue";
import TaskEditDialog from "@/components/TaskEditDialog.vue";
import TaskResourcesDialog from "@/components/TaskResourcesDialog.vue";

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

		getTasks().then((resp: Task[]) => {
			activeRequests.value = resp.filter((task: Task) => task.status !== "Completed");
			inactiveRequests.value = resp.filter((task: Task) => task.status === "Completed");

			props.requestType === "active" ?
				tasks.value = activeRequests.value.map((task: Task) => ({ task, isNew: false })) :
				tasks.value = inactiveRequests.value.map((task: Task) => ({ task, isNew: false }));
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
			taskIdToViewResources
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

			<div class="table-card">
				<TaskTable
					:tasks="tasks"
					@task-name-click="editTask"
					@view-resources="viewTaskResources"
				/>
			</div>
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
